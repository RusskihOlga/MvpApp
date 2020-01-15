package com.android.mvpauth.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.components.AppComponent;
import com.android.mvpauth.di.components.DaggerAppComponent;
import com.android.mvpauth.di.modules.AppModule;
import com.android.mvpauth.di.modules.PicassoCacheModule;
import com.android.mvpauth.di.modules.RootModule;
import com.android.mvpauth.mortar.ScreenScoper;
import com.android.mvpauth.ui.activities.DaggerRootActivity_RootComponent;
import com.android.mvpauth.ui.activities.RootActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class App extends Application {

    private static SharedPreferences sSharedPreferences;
    private static AppComponent sAppComponent;
    private MortarScope mRootScope;

    private static Context sContext;
    private MortarScope mRootActivityScope;
    private static RootActivity.RootComponent mRootActivityRootComponent;

    @Override
    public Object getSystemService(String name) {
        return mRootScope.hasService(name) ? mRootScope.getService(name) : super.getSystemService(name);
    }

    @Override
    public void onCreate() {
        Fabric.with(this, new Crashlytics());

        Realm.init(this);

        createAppComponent();
        createRootActivityComponent();

        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sContext = getApplicationContext();

        mRootScope = MortarScope.buildRootScope()
                .withService(DaggerService.SERVICE_NAME, sAppComponent)
                .build("Root");


        mRootActivityScope = mRootScope.buildChild()
                .withService(DaggerService.SERVICE_NAME, mRootActivityRootComponent)
                .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                .build(RootActivity.class.getName());

        ScreenScoper.registerScope(mRootScope);
        ScreenScoper.registerScope(mRootActivityScope);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    private void createAppComponent() {
        sAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();
    }

    private void createRootActivityComponent() {
        mRootActivityRootComponent = DaggerRootActivity_RootComponent.builder()
                .appComponent(sAppComponent)
                .rootModule(new RootModule())
                .picassoCacheModule(new PicassoCacheModule())
                .build();
    }

    public static RootActivity.RootComponent getRootActivityRootComponent() {
        return mRootActivityRootComponent;
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    public static Context getContext() {
        return sContext;
    }
}
