package com.android.mvpauth.di.modules;

import android.content.Context;

import com.android.mvpauth.data.managers.PreferencesManager;
import com.android.mvpauth.data.managers.RealmManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocalModule extends FlavorLocalModule{

    @Singleton
    @Provides
    PreferencesManager providerProferencesManager(Context context){
        return new PreferencesManager(context);
    }
}
