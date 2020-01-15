package com.android.mvpauth.mvp.views;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.android.mvpauth.data.storage.dto.UserInfoDto;

public interface IRootView extends IView{
    void showMessage(String message);
    void showError(Throwable e);
    void startForResult(Intent intent, int requestCode);
    Activity getActivity();

    void showLoad();
    void hideLoad();

    @Nullable
    IView getCurrentScreen();

    void initDrawer(UserInfoDto userInfoDto);
}
