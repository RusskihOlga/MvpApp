package com.android.mvpauth.ui.screens.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.mvpauth.R;
import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.data.storage.dto.LoginDto;
import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.scopes.AuthScope;
import com.android.mvpauth.flow.AbstractScreen;
import com.android.mvpauth.flow.Screen;
import com.android.mvpauth.mvp.models.AuthModel;
import com.android.mvpauth.mvp.presenters.AbstractPresenter;
import com.android.mvpauth.mvp.presenters.IAuthPresenter;
import com.android.mvpauth.mvp.presenters.RootPresenter;
import com.android.mvpauth.mvp.views.IRootView;
import com.android.mvpauth.ui.activities.RootActivity;
import com.android.mvpauth.ui.activities.SplashActivity;
import com.android.mvpauth.utils.App;

import javax.inject.Inject;

import dagger.Provides;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Screen(R.layout.screen_auth)
public class AuthScreen extends AbstractScreen<RootActivity.RootComponent> {

    private int mCustomState = 1;

    public int getCustomState() {
        return mCustomState;
    }

    public void setCustomState(int customState) {
        mCustomState = customState;
    }

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentRootComponent) {
        return DaggerAuthScreen_Component.builder()
                .rootComponent(parentRootComponent)
                .module(new Module())
                .build();
    }

    //region ==================== DI ====================
    @dagger.Module
    public class Module {
        @Provides
        @AuthScope
        AuthPresenter providePresenter() {
            return new AuthPresenter();
        }

        @Provides
        @AuthScope
        AuthModel provideAuthModel() {
            return new AuthModel();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @AuthScope
    public interface Component {
        void inject(AuthPresenter presenter);

        void inject(AuthView view);
    }
    //endregion

    //region ==================== Presenter ====================
    public class AuthPresenter extends AbstractPresenter<AuthView, AuthModel> implements IAuthPresenter {

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if (checkUserAuth()) {
                getView().hideLoginBtn();
            } else {
                getView().showLoginBtn();
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
            //mRootPresenter.newFabBuilder().setVisible(false).build();
        }

        @Override
        public void clickOnLogin() {
            if (getView() != null && getRootView() != null) {
                if (getView().isIdle()) {
                    getView().showLoginWithAnim();
                } else {
                    //ProgressDialog dialog = new ProgressDialog(App.getContext());
                    //dialog.show();
                    LoginDto login = new LoginDto(getView().getUserEmail(), getView().getUserPassword());
                    new Thread(() -> {
                        mModel.loginUser(login).subscribe(loginRes -> {
                            mModel.saveUserToken(loginRes.getToken());
                            getView().showIdleWithAnim();
                        }, throwable -> {
                            getRootView().showMessage("Ошибка авторизации");
                        });
                    }).start();

                    //getRootView().showMessage("request for user auth");
                }
            }
        }

        @Override
        public void clickOnVk() {
            if (getRootView() != null) {
                getRootView().showMessage("clickOnVk");
            }
        }

        @Override
        public void clickOnFb() {
            if (getRootView() != null) {
                getRootView().showMessage("clickOnFb");
            }
        }

        @Override
        public void clickOnTwitter() {
            if (getRootView() != null) {
                getRootView().showMessage("clickOnTwitter");
            }
        }

        @Override
        public void clickOnShowCatalog() {
            if (getView() != null && getRootView() != null) {
                //getRootView().showMessage("Показать каталог");

                if (getRootView() instanceof SplashActivity) {
                    ((SplashActivity) getRootView()).startRootActivity();
                } else {
                    // TODO: 03.12.2016 show catalog screen
                }
            }
        }

        @Override
        public boolean checkUserAuth() {
            return mModel.isAuthUser();
        }

        @Override
        public boolean isValidateEmail(CharSequence email) {
            return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        @Override
        public boolean isValidatePassword(String password) {
            return password.length() >= 8;
        }
    }
    //endregion
}
