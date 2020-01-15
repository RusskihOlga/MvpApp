package com.android.mvpauth.di.components;

import com.android.mvpauth.di.modules.PicassoCacheModule;
import com.android.mvpauth.di.scopes.RootScope;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = PicassoCacheModule.class)
@RootScope
public interface PicassoComponent {
    Picasso getPicasso();
}
