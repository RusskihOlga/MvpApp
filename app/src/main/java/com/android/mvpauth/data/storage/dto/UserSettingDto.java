package com.android.mvpauth.data.storage.dto;

public class UserSettingDto {

    private boolean orderNotification;
    private boolean prompNotification;

    public UserSettingDto(boolean orderNotification, boolean prompNotification) {
        this.orderNotification = orderNotification;
        this.prompNotification = prompNotification;
    }

    public boolean isOrderNotification() {
        return orderNotification;
    }

    public boolean isPromoNotification() {
        return prompNotification;
    }
}
