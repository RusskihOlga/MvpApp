package com.android.mvpauth.di.components;

import android.content.Context;

import com.android.mvpauth.di.modules.AppModule;

import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponent {
    Context getContext();
}
