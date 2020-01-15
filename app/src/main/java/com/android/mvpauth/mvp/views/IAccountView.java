package com.android.mvpauth.mvp.views;

public interface IAccountView extends IView{

    void changeState();
    void showEditState();
    void showPreviewState();
    void showPhotoSourceDialog();
    String getUserName();
    String getUserPhone();
}
