package com.android.mvpauth.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.mvpauth.data.network.res.ProductRes;
import com.android.mvpauth.data.storage.dto.ProductDTO;
import com.android.mvpauth.utils.ConstantManager;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {

    public static final String PROFILE_FULL_NAME_KEY = "PROFILE_FULL_NAME_KEY";
    public static final String PROFILE_AVATAR_KEY = "PROFILE_AVATAR_KEY";
    public static final String PROFILE_PHONE_KEY = "PROFILE_PHONE_KEY";
    public static final String NOTIFICATION_ORDER_KEY = "NOTIFICATION_ORDER_KEY";
    public static final String NOTIFICATION_PROMO_KEY = "NOTIFICATION_PROMO_KEY";
    private static final String PRODUCT_LAST_UPDATE_KEY = "PRODUCT_LAST_UPDATE_KEY";

    private SharedPreferences mSharedPreferences;
    private Moshi mMoshi;
    private JsonAdapter<ProductDTO> mProductJsonAdapter;

    public PreferencesManager(Context context) {
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mMoshi = new Moshi.Builder().build();
        mProductJsonAdapter = mMoshi.adapter(ProductDTO.class);
    }

    public String getLastProductUpdate() {
        return mSharedPreferences.getString(PRODUCT_LAST_UPDATE_KEY, "Thu, 01 Jan 1970 00:00:00 GMT");
    }

    public void saveLastProductUpdate(String lastModified) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PRODUCT_LAST_UPDATE_KEY, lastModified);
        editor.apply();
    }

    public void saveAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }

    public boolean isToken() {
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN_KEY, "null").equals("null");
    }

    public void updateBasket(int count){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(ConstantManager.BASKET_KEY, getCountBasket() + count);
        editor.apply();
    }

    public int getCountBasket(){
        return mSharedPreferences.getInt(ConstantManager.BASKET_KEY, 0);
    }

    public void saveUserAvatar(String avatarUrl) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_AVATAR_KEY, avatarUrl);
        editor.apply();
    }

    public String getUserAvatar() {
        return mSharedPreferences.getString(PROFILE_AVATAR_KEY, "http://s018.radikal.ru/i516/1611/5a/76d466ab9d20.png");
    }

    public String getUserName() {
        return mSharedPreferences.getString(PROFILE_FULL_NAME_KEY, "NoName");
    }
    public String getPhoneUser() {
        return mSharedPreferences.getString(PROFILE_PHONE_KEY, "89232498022");
    }

    public void saveProfile(String name, String phone, String avatar) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_FULL_NAME_KEY, name);
        editor.putString(PROFILE_PHONE_KEY, phone);
        editor.putString(PROFILE_AVATAR_KEY, avatar);
        editor.apply();
    }
}
