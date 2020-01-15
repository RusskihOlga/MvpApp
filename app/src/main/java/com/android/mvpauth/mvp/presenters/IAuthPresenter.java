package com.android.mvpauth.mvp.presenters;

import android.support.annotation.Nullable;

import com.android.mvpauth.mvp.views.IAuthView;

public interface IAuthPresenter {

    void clickOnLogin();
    void clickOnVk();
    void clickOnFb();
    void clickOnTwitter();
    void clickOnShowCatalog();

    boolean checkUserAuth();

    boolean isValidateEmail(CharSequence email);
    boolean isValidatePassword(String password);
}
