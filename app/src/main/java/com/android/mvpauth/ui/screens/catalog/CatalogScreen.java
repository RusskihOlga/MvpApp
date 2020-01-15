package com.android.mvpauth.ui.screens.catalog;

import android.content.Context;
import android.os.Bundle;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.scopes.DaggerScope;
import com.android.mvpauth.flow.AbstractScreen;
import com.android.mvpauth.flow.Screen;
import com.android.mvpauth.mvp.models.CatalogModel;
import com.android.mvpauth.mvp.presenters.AbstractPresenter;
import com.android.mvpauth.mvp.presenters.ICatalogPresenter;
import com.android.mvpauth.mvp.presenters.MenuItemHolder;
import com.android.mvpauth.mvp.presenters.RootPresenter;
import com.android.mvpauth.ui.activities.RootActivity;
import com.android.mvpauth.ui.screens.auth.AuthScreen;
import com.android.mvpauth.ui.screens.product.ProductScreen;
import com.squareup.picasso.Picasso;

import dagger.Provides;
import flow.Flow;
import mortar.MortarScope;
import rx.Subscriber;
import rx.Subscription;

@Screen(R.layout.screen_catalog)
public class CatalogScreen extends AbstractScreen<RootActivity.RootComponent> {

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCatalogScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region ==================== DI ====================
    @dagger.Module
    public class Module {

        @Provides
        @DaggerScope(CatalogScreen.class)
        CatalogModel provideCatalogModule() {
            return new CatalogModel();
        }

        @Provides
        @DaggerScope(CatalogScreen.class)
        CatalogPresenter provideCatalogPresenter() {
            return new CatalogPresenter();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(CatalogScreen.class)
    public interface Component {
        void inject(CatalogPresenter catalogPresenter);

        void inject(CatalogView view);

        CatalogModel getCatalogModel();

        Picasso getPicasso();

        RootPresenter getRootPresenter();
    }
    //endregion

    //region ==================== Presenter ====================
    public class CatalogPresenter extends AbstractPresenter<CatalogView, CatalogModel> implements ICatalogPresenter {

        private int lastPagerPosition;

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            mCompSubs.add(subscribeOnProductDtoObs());
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle("Каталог")
                    .setBackArrow(false)
                    .addAction(new MenuItemHolder("Каталог", R.drawable.ic_shopping_basket_black_24dp,
                            item -> {
                                getRootView().showMessage("Перейти в корзину");
                                return true;
                            })).build();
        }

        @Override
        protected void initFab() {
            mRootPresenter.newFabBuilder().setVisible(false).build();
        }

        @Override
        public void dropView(CatalogView view) {
            super.dropView(view);
            if (getView() != null)
                lastPagerPosition = getView().getCurrentPagerPosition();
        }

        //region ======================== subscribtion ========================
        private Subscription subscribeOnProductDtoObs() {
            if (getRootView() != null && getView() != null) {
                getRootView().showLoad();
            }

            return mModel.getProductObs().subscribe(new RealmSubscriber());
        }

        private class RealmSubscriber extends Subscriber<ProductRealm> {
            CatalogAdapter mAdapter = getView().getAdapter();

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (getRootView() != null) getRootView().showError(e);
            }

            @Override
            public void onNext(ProductRealm productRealm) {
                mAdapter.addItem(productRealm);
                if (mAdapter.getCount() - 1 == lastPagerPosition) {
                    getRootView().hideLoad();
                    getView().showCatalogView();
                }
            }
        }
        //endregion

        @Override
        public void clickOnBuyButton(int position) {
            if (getView() != null) {
                if (checkUserAuth()) {
                    getView().getCurrentProductView().startAddToCardAnim();
                } else {
                    Flow.get(getView()).set(new AuthScreen());
                }
            }
        }

        @Override
        public boolean checkUserAuth() {
            return mModel.isUserAuth();
        }
    }
    //endregion

    public static class Factory {
        public static Context createProductContext(ProductRealm product, Context parentContext) {
            MortarScope parentScope = MortarScope.getScope(parentContext);
            MortarScope childScope = null;
            ProductScreen screen = new ProductScreen(product);
            String scopeName = String.format("%s_%s", screen.getScopeName(), product.getId());

            if (parentScope.findChild(scopeName) == null) {
                childScope = parentScope.buildChild()
                        .withService(DaggerService.SERVICE_NAME,
                                screen.createScreenComponent(DaggerService.<CatalogScreen.Component>getDaggerComponent(parentContext)))
                        .build(scopeName);
            } else {
                childScope = parentScope.findChild(scopeName);
            }
            return childScope.createContext(parentContext);
        }
    }
}
