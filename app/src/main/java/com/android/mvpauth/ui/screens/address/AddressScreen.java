package com.android.mvpauth.ui.screens.address;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.UserAddressDTO;
import com.android.mvpauth.data.storage.realm.AddressRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.scopes.AddressScope;
import com.android.mvpauth.flow.AbstractScreen;
import com.android.mvpauth.flow.Screen;
import com.android.mvpauth.mvp.models.AccountModel;
import com.android.mvpauth.mvp.presenters.AbstractPresenter;
import com.android.mvpauth.mvp.presenters.IAddressPresenter;
import com.android.mvpauth.ui.screens.account.AccountScreen;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import flow.TreeKey;
import io.realm.Realm;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Screen(R.layout.screen_address)
public class AddressScreen extends AbstractScreen<AccountScreen.Component> implements TreeKey {

    @Nullable
    private UserAddressDTO mAddressDTO;

    public AddressScreen(@Nullable UserAddressDTO addressDTO) {
        mAddressDTO = addressDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (mAddressDTO != null) {
            return o instanceof AddressScreen && mAddressDTO.equals(((AddressScreen) o).mAddressDTO);
        } else {
            return super.equals(o);
        }
    }

    @Override
    public int hashCode() {
        return mAddressDTO != null ? mAddressDTO.hashCode() : super.hashCode();
    }

    @Override
    public Object createScreenComponent(AccountScreen.Component parentComponent) {
        return DaggerAddressScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @Override
    public Object getParentKey() {
        return new AccountScreen();
    }

    //region ==================== DI ====================

    @dagger.Module
    public class Module {
        @Provides
        @AddressScope
        AddressPresenter provideAddressPresenter() {
            return new AddressPresenter();
        }
    }

    @dagger.Component(dependencies = AccountScreen.Component.class, modules = Module.class)
    @AddressScope
    public interface Component {
        void inject(AddressPresenter presenter);

        void inject(AddressView view);
    }

    //endregion

    //region ==================== Presenter ====================
    public class AddressPresenter extends AbstractPresenter<AddressView, AccountModel> implements IAddressPresenter {

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if (mAddressDTO != null && getView() != null) {
                getView().initView(mAddressDTO);
            }
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
            mRootPresenter.newFabBuilder().setVisible(false).build();
        }

        @Override
        public void clickOnAddAddress() {
            // TODO: 04.12.2016 save address in model
            if (getView() != null) {
                Realm realm = Realm.getDefaultInstance();
                AddressRealm addressRealm;
                if (mAddressDTO == null) {
                    addressRealm = new AddressRealm(getView().getUserAddress());
                    int key;
                    try {
                        key = realm.where(AddressRealm.class).max("id").intValue() + 1;
                    } catch (Exception e) {
                        key = 1;
                    }
                    addressRealm.setId(key);
                } else {
                    addressRealm = new AddressRealm(mAddressDTO);
                }

                realm.executeTransaction(realm1 -> realm1.insertOrUpdate(addressRealm));
                realm.close();
                Flow.get(getView()).goBack();
            }
        }
    }
    //endregion
}
