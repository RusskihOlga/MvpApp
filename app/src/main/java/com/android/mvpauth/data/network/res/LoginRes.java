package com.android.mvpauth.data.network.res;

import com.squareup.moshi.Json;

/**
 * Created by Ольга on 19.02.2017.
 */
public class LoginRes {
    @Json(name = "_id")
    private String id;
    private String token;

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}
