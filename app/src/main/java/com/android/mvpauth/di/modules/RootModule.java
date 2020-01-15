package com.android.mvpauth.di.modules;

import com.android.mvpauth.di.scopes.RootScope;
import com.android.mvpauth.mvp.models.AccountModel;
import com.android.mvpauth.mvp.presenters.RootPresenter;

import dagger.Provides;

/**
 * Created by Евгения on 03.12.2016.
 */
@dagger.Module
public class RootModule {
    @Provides
    @RootScope
    RootPresenter providerRootPresenter() {
        return new RootPresenter();
    }

    @Provides
    @RootScope
    AccountModel providerAccountModel() {
        return new AccountModel();
    }
}
