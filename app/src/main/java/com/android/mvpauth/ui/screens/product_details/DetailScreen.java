package com.android.mvpauth.ui.screens.product_details;

import android.os.Bundle;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.scopes.DaggerScope;
import com.android.mvpauth.flow.AbstractScreen;
import com.android.mvpauth.flow.Screen;
import com.android.mvpauth.mvp.models.DetailModel;
import com.android.mvpauth.mvp.presenters.AbstractPresenter;
import com.android.mvpauth.mvp.presenters.MenuItemHolder;
import com.android.mvpauth.mvp.presenters.RootPresenter;
import com.android.mvpauth.ui.screens.catalog.CatalogScreen;
import com.squareup.picasso.Picasso;

import dagger.Provides;
import flow.TreeKey;
import mortar.MortarScope;

@Screen(R.layout.screen_details)
public class DetailScreen extends AbstractScreen<CatalogScreen.Component> implements TreeKey {

    private final ProductRealm mProductRealm;

    public DetailScreen(ProductRealm mProduct) {
        mProductRealm = mProduct;
    }

    @Override
    public Object createScreenComponent(CatalogScreen.Component parentComponent) {
        return DaggerDetailScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @Override
    public Object getParentKey() {
        return new CatalogScreen();
    }

    //region  ======================= DI  =======================
    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(DetailScreen.class)
        DetailPresenter provideDetailPresenter() {
            return new DetailPresenter(mProductRealm);
        }

        @Provides
        @DaggerScope(DetailScreen.class)
        DetailModel provideDetailModel() {
            return new DetailModel();
        }
    }

    @dagger.Component(dependencies = CatalogScreen.Component.class, modules = DetailScreen.Module.class)
    @DaggerScope(DetailScreen.class)
    public interface Component {
        void inject(DetailPresenter presenter);
        void inject(DetailView view);

        DetailModel getDetailModel();
        RootPresenter getRootPresenter();
        Picasso getPicasso();
    }

    public class DetailPresenter extends AbstractPresenter<DetailView, DetailModel> {

        private final ProductRealm mProduct;

        public DetailPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle(mProduct.getProductName())
                    .setBackArrow(true)
                    .addAction(new MenuItemHolder("В корзину", R.drawable.ic_shopping_basket_black_24dp, item -> {
                        if (getRootView() != null) {
                            getRootView().showMessage("Перейти в корзину");
                        }
                        return true;
                    }))
                    .setTab(getView().getViewPager())
                    .build();
        }

        @Override
        protected void initFab() {

        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            getView().initView(mProduct);
        }
    }
    //endregion
}
