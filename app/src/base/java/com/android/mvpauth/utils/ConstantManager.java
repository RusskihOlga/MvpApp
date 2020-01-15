package com.android.mvpauth.utils;

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

    public static final int MIN_CONSUMER_COUNT = 1;
    public static final int MAX_CONSUMER_COUNT = 3;
    public static final int LOAD_FACTOR = 3;
    public static final int KEEP_ALIVE = 120;
    public static final int INITIAL_BACK_OFF_IN_MS = 1000;
    public static final int RETRY_REQUEST_COUNT = 5;
    public static final int UPDATE_DAtA_INTERVAL = 30;
    public static final int RETRY_REQUEST_BASE_DELAY = 500;

    public static final String BASE_URL = "https://skba1.mgbeta.ru/api/v1/";

    public static String AUTH_TOKEN_KEY= "AUTH_TOKEN_KEY";
    public static String BASKET_KEY= "BASKET_KEY";
    public static final String FILE_PROVIDER_AUTHORITY = "com.softdesign.mvpauth.fileprovider";
    public static final String LAST_MODIFIED_HEADER = "Last-Modified";
    public static final String IF_MODIFIED_HEADER = "If-Modified-Since";
}
