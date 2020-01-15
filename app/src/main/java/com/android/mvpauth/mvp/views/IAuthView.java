package com.android.mvpauth.mvp.views;

public interface IAuthView extends IView{

    void showLoginBtn();
    void hideLoginBtn();

    void showLoad();
    void hideLoad();

    void showCatalogScreen();

    String getUserEmail();
    String getUserPassword();

    boolean isIdle();
}
