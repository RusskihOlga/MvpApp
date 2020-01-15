package com.android.mvpauth.di.modules;

import com.android.mvpauth.data.network.RestService;
import com.android.mvpauth.utils.ConstantManager;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.moshi.Moshi;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    OkHttpClient providerOkhttpClient(){
        return createClient();
    }

    @Provides
    @Singleton
    Retrofit providerRetrofit(OkHttpClient okHttpClient){
        return createRetrofit(okHttpClient);
    }

    @Provides
    @Singleton
    RestService providerRestService(Retrofit retrofit){
        return retrofit.create(RestService.class);
    }

    private Retrofit createRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(ConstantManager.BASE_URL)
                .addConverterFactory(createConvertFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//add rx adapter
                .client(okHttpClient)
                .build();
    }

    private Converter.Factory createConvertFactory() {
        return MoshiConverterFactory.create(new Moshi.Builder().build());
    }

    private OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(ConstantManager.MAX_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(ConstantManager.MAX_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(ConstantManager.MAX_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }
}
