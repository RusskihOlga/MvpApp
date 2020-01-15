package com.android.mvpauth.mvp.views;

import com.android.mvpauth.data.storage.dto.UserAddressDTO;

public interface IAddressView extends IView{
    void showInputError();
    UserAddressDTO getUserAddress();
}
