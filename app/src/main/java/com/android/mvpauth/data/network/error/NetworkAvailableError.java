package com.android.mvpauth.data.network.error;

public class NetworkAvailableError extends Throwable{
    public NetworkAvailableError() {
        super("Интернет недоступен, попрубуйте позже");
    }
}
