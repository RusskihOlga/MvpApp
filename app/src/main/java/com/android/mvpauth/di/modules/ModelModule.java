package com.android.mvpauth.di.modules;

import com.android.mvpauth.data.managers.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ModelModule extends FlavorModelModule{

    @Provides
    @Singleton
    DataManager providerDataManager(){
        return DataManager.getInstance();
    }
}
