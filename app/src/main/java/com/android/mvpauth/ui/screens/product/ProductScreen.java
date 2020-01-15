package com.android.mvpauth.ui.screens.product;

import android.os.Bundle;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.ProductDTO;
import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.scopes.DaggerScope;
import com.android.mvpauth.di.scopes.ProductScope;
import com.android.mvpauth.flow.AbstractScreen;
import com.android.mvpauth.flow.Screen;
import com.android.mvpauth.mvp.models.CatalogModel;
import com.android.mvpauth.mvp.presenters.AbstractPresenter;
import com.android.mvpauth.mvp.presenters.IProductPresenter;
import com.android.mvpauth.mvp.presenters.RootPresenter;
import com.android.mvpauth.ui.activities.RootActivity;
import com.android.mvpauth.ui.screens.catalog.CatalogScreen;
import com.android.mvpauth.ui.screens.product_details.DetailScreen;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Screen(R.layout.screen_product)
public class ProductScreen extends AbstractScreen<CatalogScreen.Component> {

    private ProductRealm mProductRealm;

    public ProductScreen(ProductRealm product) {
        mProductRealm = product;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ProductScreen && mProductRealm.equals(((ProductScreen) o).mProductRealm);
    }

    @Override
    public int hashCode() {
        return mProductRealm.hashCode();
    }

    @Override
    public Object createScreenComponent(CatalogScreen.Component parentComponent) {
        return DaggerProductScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    //region ==================== DI ====================
    @dagger.Module
    public class Module {
        @Provides
        @ProductScope
        ProductPresenter provideProductPresenter() {
            return new ProductPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = CatalogScreen.Component.class, modules = Module.class)
    @ProductScope
    public interface Component {
        void inject(ProductView view);

        void inject(ProductPresenter presenter);

        RootPresenter getRootPresenter();
    }
    //endregion

    //region ==================== Presenter ====================

    public class ProductPresenter extends AbstractPresenter<ProductView, CatalogModel> implements IProductPresenter {

        @Inject
        CatalogModel mCatalogModel;
        private ProductRealm mProduct;
        private RealmChangeListener mListener;
        private boolean isZoomed;

        public ProductPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            /*if (savedInstanceState != null) {
                isZoomed = savedInstanceState.getBoolean("zoomed");
            }*/
            if (getView() != null && mProduct.isValid()) {
                getView().showProductView(new ProductDTO(mProduct));

                mListener = element -> {
                    if (getView() != null) {
                        getView().showProductView(new ProductDTO(mProduct));
                    }
                };
                mProduct.addChangeListener(mListener);
            } else {
                // TODO: 21.01.2017 implement me
            }
        }

        /*@Override
        protected void onSave(Bundle outState) {
            super.onSave(outState);
            outState.putBoolean("zoomed", isZoomed);
        }*/

        public boolean isZoomed() {
            return isZoomed;
        }

        public void setZoomed(boolean isZoomed) {
            this.isZoomed = isZoomed;
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

        }

        @Override
        public void dropView(ProductView view) {
            mProduct.removeChangeListener(mListener);
            super.dropView(view);
        }

        @Override
        public void clickOnPlus() {
            if (getView() != null) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> mProduct.add());
                realm.close();
            }
        }

        @Override
        public void clickOnMinus() {
            if (getView() != null) {
                if (mProduct.getCount() > 0) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> mProduct.remove());
                    realm.close();
                }
            }
        }

        @Override
        public void clickFavorite() {
            Realm realm = Realm.getDefaultInstance();
            if (realm.isInTransaction())
                realm.executeTransaction(realm1 -> mProduct.changeFavorite());
            realm.close();
        }

        @Override
        public void clickShowMore() {
            Flow.get(getView()).set(new DetailScreen(mProduct));
        }
    }

    //endregion
}
