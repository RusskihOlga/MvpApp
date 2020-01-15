package com.android.mvpauth.di.components;

import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.di.modules.LocalModule;
import com.android.mvpauth.di.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(dependencies = AppComponent.class, modules = {NetworkModule.class, LocalModule.class})
public interface DataManagerComponent {
    void  inject(DataManager dataManager);
}
