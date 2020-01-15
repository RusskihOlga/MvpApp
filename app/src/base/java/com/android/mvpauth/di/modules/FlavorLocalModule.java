package com.android.mvpauth.di.modules;

import android.content.Context;

import com.android.mvpauth.data.managers.RealmManager;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FlavorLocalModule {

    @Singleton
    @Provides
    RealmManager provideRealmManager(Context context) {
        Stetho.initialize(Stetho.newInitializerBuilder(context)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
                .build());
        return new RealmManager();
    }
}
