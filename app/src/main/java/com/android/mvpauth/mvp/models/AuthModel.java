package com.android.mvpauth.mvp.models;

import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.data.network.res.LoginRes;
import com.android.mvpauth.data.storage.dto.LoginDto;

import rx.Observable;

public class AuthModel extends AbstractModel{

    public AuthModel() {
    }

    public boolean isAuthUser(){
        // TODO: 21.10.2016 search token in shared
        return false;
    }

    public Observable<LoginRes> loginUser(LoginDto login){
        return DataManager.getInstance().authUser(login);
    }

    public void saveUserToken(String token) {
        DataManager.getInstance().saveAuthUser(token);
    }
}
