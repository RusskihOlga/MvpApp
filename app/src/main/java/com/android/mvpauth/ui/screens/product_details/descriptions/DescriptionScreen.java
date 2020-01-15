package com.android.mvpauth.ui.screens.product_details.descriptions;

import android.os.Bundle;
import android.view.View;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.DescriptionDto;
import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.scopes.DaggerScope;
import com.android.mvpauth.flow.AbstractScreen;
import com.android.mvpauth.flow.Screen;
import com.android.mvpauth.mvp.models.DetailModel;
import com.android.mvpauth.mvp.presenters.AbstractPresenter;
import com.android.mvpauth.mvp.presenters.RootPresenter;
import com.android.mvpauth.ui.screens.product_details.DetailScreen;

import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import mortar.MortarScope;

@Screen(R.layout.screen_description)
public class DescriptionScreen extends AbstractScreen<DetailScreen.Component> {

    private ProductRealm mProductRealm;

    public DescriptionScreen(ProductRealm mProductRealm) {
        this.mProductRealm = mProductRealm;
    }

    @Override
    public Object createScreenComponent(DetailScreen.Component parentComponent) {
        return DaggerDescriptionScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(DescriptionScreen.class)
        DescriptionPresenter provideDescriptionPresenter() {
            return new DescriptionPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = DetailScreen.Component.class, modules = Module.class)
    @DaggerScope(DescriptionScreen.class)
    public interface Component {
        void inject(DescriptionPresenter presenter);

        void inject(DescriptionView view);
    }

    public class DescriptionPresenter extends AbstractPresenter<DescriptionView, DetailModel> {

        private final ProductRealm mProduct;
        private RealmChangeListener mListener;

        public DescriptionPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            getView().initView(new DescriptionDto(mProduct));

            mListener = element -> {
                if (getView() != null) {
                    getView().initView(new DescriptionDto(mProduct));
                }
            };

            mProduct.addChangeListener(mListener);
        }

        @Override
        public void dropView(DescriptionView view) {
            super.dropView(view);
            mProduct.removeChangeListener(mListener);
        }

        @Override
        protected void initActionBar() {
            //empty
        }

        @Override
        protected void initFab() {

        }

        public void clickOnPlus() {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> mProduct.add());
            realm.close();
        }

        public void clickOnMinus() {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> mProduct.remove());
            realm.close();
        }
    }
}
