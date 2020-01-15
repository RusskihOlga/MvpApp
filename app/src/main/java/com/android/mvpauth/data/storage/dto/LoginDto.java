package com.android.mvpauth.data.storage.dto;

/**
 * Created by Ольга on 19.02.2017.
 */

public class LoginDto {
    private String login;
    private String password;

    public LoginDto(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
