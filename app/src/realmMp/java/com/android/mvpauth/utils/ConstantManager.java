package com.android.mvpauth.utils;

import com.android.mvpauth.BuildConfig;

/**
 * Created by Евгения on 22.10.2016.
 */

public class ConstantManager {

    public static final int MAX_CONNECTION_TIMEOUT = 5000;
    public static final int MAX_READ_TIMEOUT = 5000;
    public static final int MAX_WRITE_TIMEOUT = 5000;

    public static final int REQUEST_PERMISSIONS_CAMERA = 3000;
    public static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3001;

    public static final int REQUEST_PROFILE_PHOTO_PICKER = 1001;
    public static final int REQUEST_PROFILE_PHOTO_CAMERA = 1002;

    public static final String BASE_URL = "https://skba1.mgbeta.ru/api/v1/";

    public static String AUTH_TOKEN_KEY = "AUTH_TOKEN_KEY";
    public static String BASKET_KEY = "BASKET_KEY";
    public static final String FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";
    public static final String LAST_MODIFIED_HEADER = "Last-Modified";
    public static final String IF_MODIFIED_HEADER = "If-Modified-Since";

    public static final String REALM_USER = "russkih.1994@mail.ru";
    public static final String REALM_PASSWORD = "280894";
    public static final String REALM_AUTH_URL = "http://192.168.0.11:9080/auth";
    public static final String REALM_DB_URL = "realm://192.168.0.11:9080/~/default";

}
