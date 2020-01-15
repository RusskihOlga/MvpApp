package com.android.mvpauth.ui.screens.product_details.comments;

import android.os.Bundle;
import android.view.View;

import com.android.mvpauth.BuildConfig;
import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.CommentDto;
import com.android.mvpauth.data.storage.realm.CommentRealm;
import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.scopes.DaggerScope;
import com.android.mvpauth.flow.AbstractScreen;
import com.android.mvpauth.flow.Screen;
import com.android.mvpauth.mvp.models.DetailModel;
import com.android.mvpauth.mvp.presenters.AbstractPresenter;
import com.android.mvpauth.ui.screens.product_details.DetailScreen;

import java.util.List;

import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import mortar.MortarScope;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@Screen(R.layout.screen_comment)
public class CommentScreen extends AbstractScreen<DetailScreen.Component> {

    private final ProductRealm mProductRealm;

    public CommentScreen(ProductRealm productRealm) {
        mProductRealm = productRealm;
    }

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(CommentScreen.class)
        CommentsPresenter provideCommentsPresenter() {
            return new CommentsPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = DetailScreen.Component.class, modules = Module.class)
    @DaggerScope(CommentScreen.class)
    public interface Component {
        void inject(CommentsPresenter presenter);

        void inject(CommentsView view);

        void inject(CommentsAdapter adapter);
    }

    @Override
    public Object createScreenComponent(DetailScreen.Component parentComponent) {
        return DaggerCommentScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    public class CommentsPresenter extends AbstractPresenter<CommentsView, DetailModel> {

        private final ProductRealm mProduct;
        private RealmChangeListener mListener;

        public CommentsPresenter(ProductRealm mProduct) {
            this.mProduct = mProduct;
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void initActionBar() {

        }

        @Override
        protected void initFab() {
            View.OnClickListener listener = v -> clickOnAddComment();
            mRootPresenter.newFabBuilder()
                    .setVisible(true)
                    .setIconResId(R.drawable.ic_add_white_24dp)
                    .setListener(listener)
                    .build();
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            mListener = new RealmChangeListener<ProductRealm>() {
                @Override
                public void onChange(ProductRealm element) {
                    CommentsPresenter.this.updateProductList(element);
                }
            };
            mProduct.addChangeListener(mListener);

            RealmList<CommentRealm> comments = mProduct.getCommentRealm();
            Observable<CommentDto> commentsObs = Observable.from(comments)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(CommentDto::new);
            mCompSubs.add(subscribe(commentsObs, new ViewSubscriber<CommentDto>() {
                @Override
                public void onNext(CommentDto commentDto) {
                    getView().getAdapter().addItem(commentDto);
                }
            }));
            getView().initView();
        }

        @Override
        public void dropView(CommentsView view) {
            mProduct.removeChangeListener(mListener);
            super.dropView(view);
        }

        private void updateProductList(ProductRealm element) {
            Observable<List<CommentDto>> obs = Observable.from(element.getCommentRealm())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(CommentDto::new)
                    .toList();

            mCompSubs.add(subscribe(obs, new ViewSubscriber<List<CommentDto>>() {
                @Override
                public void onNext(List<CommentDto> commentDtos) {
                    getView().getAdapter().reloadAdapter(commentDtos);
                }
            }));

        }

        public void clickOnAddComment() {
            getView().showAddCommentDialog();
        }

        public void addComment(CommentRealm commentRealm) {
            switch (BuildConfig.FLAVOR) {
                case "base":
                    mModel.sendComment(mProduct.getId(), commentRealm);
                    break;
                case "realmMp":
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> mProduct.getCommentRealm().add(commentRealm));
                    realm.close();
                    break;
            }
        }
    }
}
