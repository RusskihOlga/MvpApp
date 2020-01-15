package com.android.mvpauth.di.modules;

import android.content.Context;
import android.util.Log;

import com.android.mvpauth.data.managers.RealmManager;
import com.android.mvpauth.utils.ConstantManager;
import com.facebook.stetho.Stetho;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class FlavorLocalModule {

    private static final String TAG = "REALM_MP";

    @Singleton
    @Provides
    RealmManager provideRealmManager(Context context) {
        Log.e(TAG, "provideRealmManager init: ");
        Stetho.initializeWithDefaults(context);

        Observable.create(new Observable.OnSubscribe<SyncUser>(){

            @Override
            public void call(Subscriber<? super SyncUser> subscriber) {
                SyncCredentials myCredentials = SyncCredentials.usernamePassword(ConstantManager.REALM_USER, ConstantManager.REALM_PASSWORD, false);
                //sync
                if (!subscriber.isUnsubscribed()) {
                    try {
                        subscriber.onNext(SyncUser.login(myCredentials, ConstantManager.REALM_AUTH_URL));
                        subscriber.onCompleted();
                    }catch (Exception e) {
                        subscriber.onError(e);
                    }
                }

                //async
                /*if (!subscriber.isUnsubscribed()) {
                    SyncUser.loginAsync(myCredentials, ConstantManager.REALM_AUTH_URL, new SyncUser.Callback() {
                        @Override
                        public void onSuccess(SyncUser user) {
                            subscriber.onNext(user);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(ObjectServerError error) {
                            subscriber.onError(error);
                        }
                    });
                }*/
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(syncUser -> {
            SyncConfiguration syncConfig = new SyncConfiguration.Builder(syncUser, ConstantManager.REALM_DB_URL).build();
            Realm.setDefaultConfiguration(syncConfig);
            Log.e(TAG, "set sync config for realm");
        });
        return new RealmManager();
    }


}
