package com.android.mvpauth.di.components;

import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.di.modules.ModelModule;
import com.android.mvpauth.mvp.models.AbstractModel;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = ModelModule.class)
@Singleton
public interface ModelComponent {
    void inject(AbstractModel abstractModel);
}
