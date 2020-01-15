package com.android.mvpauth.data.managers;


import android.util.Log;

import com.android.mvpauth.data.network.RestCallTransformer;
import com.android.mvpauth.data.network.RestService;
import com.android.mvpauth.data.network.res.AvatarUrlRes;
import com.android.mvpauth.data.network.res.CommentRes;
import com.android.mvpauth.data.network.res.LoginRes;
import com.android.mvpauth.data.network.res.ProductRes;
import com.android.mvpauth.data.storage.dto.LoginDto;
import com.android.mvpauth.data.storage.dto.UserAddressDTO;
import com.android.mvpauth.data.storage.realm.AddressRealm;
import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.components.DaggerDataManagerComponent;
import com.android.mvpauth.di.components.DataManagerComponent;
import com.android.mvpauth.di.modules.LocalModule;
import com.android.mvpauth.di.modules.NetworkModule;
import com.android.mvpauth.utils.App;
import com.android.mvpauth.utils.ConstantManager;
import com.android.mvpauth.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.realm.Realm;
import okhttp3.MultipartBody;
import retrofit2.Retrofit;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DataManager {

    private static final String TAG = "DataManager";
    private static DataManager outInstance = new DataManager();

    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    RestService mRestService;
    @Inject
    Retrofit mRetrofit;
    @Inject
    RealmManager mRealmManager;

    private ArrayList<UserAddressDTO> mUserAddressList;

    public static DataManager getInstance() {
        return outInstance;
    }

    public DataManager() {
        DataManagerComponent dataManagerComponent = DaggerDataManagerComponent.builder()
                .appComponent(App.getAppComponent())
                .localModule(new LocalModule())
                .networkModule(new NetworkModule())
                .build();
        DaggerService.registerComponent(DataManagerComponent.class, dataManagerComponent);
        dataManagerComponent.inject(this);

        updateLocalDataWithTimer();//for example
    }

    private void updateLocalDataWithTimer() {
        Log.e(TAG, "LOCAL UPDATE start: " + new Date());
        Observable.interval(ConstantManager.UPDATE_DAtA_INTERVAL, TimeUnit.SECONDS)
                .flatMap(aLong -> NetworkStatusChecker.isInernetAvailable())
                .filter(aBoolean -> aBoolean)
                .flatMap(aBoolean -> getProductObsFromNetworkRetry())
                .subscribe(productRealm -> {
                    Log.e(TAG, "LOCAL UPDATE complete: ");
                }, throwable -> {
                    throwable.printStackTrace();
                    Log.e(TAG, "LOCALE UPDATE error: " + throwable.getMessage());
                });
    }

    public Observable<ProductRealm> getProductObsFromNetworkRetry() {
        return mRestService.getProductResObs(mPreferencesManager.getLastProductUpdate())
                .compose(new RestCallTransformer<>())//трансформируем response выбрасываем ошибку в случае не доступности интернета
                .flatMap(Observable::from) // преобразуем список товаров в последовательность товаров
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(productRes -> {
                    if (!productRes.isActive()) {
                        mRealmManager.deleteFromRealm(ProductRealm.class, productRes.getId());
                    }
                })
                .filter(ProductRes::isActive)//пропустить дальше только активные продукты
                .doOnNext(productRes -> mRealmManager.saveProductResponseToRealm(productRes))//сохраняем на диск только активные товары
                .retryWhen(errorObservable ->
                        errorObservable
                                .zipWith(Observable.range(1, ConstantManager.RETRY_REQUEST_COUNT), (throwable, retryCount) -> retryCount)
                                .doOnNext(retryCount -> Log.e(TAG, "LOCAL UPDATE request retry count: " + retryCount + " " + new Date()))
                                .map(retryCount -> ((long) (ConstantManager.RETRY_REQUEST_BASE_DELAY * Math.pow(Math.E, retryCount))))
                                .doOnNext(delay -> Log.e(TAG, "LOCAL UPDATE delay: " + delay))
                                .flatMap(delay -> Observable.timer(delay, TimeUnit.MILLISECONDS))
                )
                .flatMap(productRes -> Observable.empty());
    }

    public Observable<LoginRes> authUser(LoginDto login) {
        return mRestService.authUserObs(login);
    }

    public PreferencesManager getPreferencesManager()  {
        return mPreferencesManager;
    }

    public Observable<CommentRes> sendComment(String productId, CommentRes comment) {
        return mRestService.sendComment(productId, comment);
    }

    public void updateCountProductBasket(int count) {
        mPreferencesManager.updateBasket(count);
    }

    public boolean isAuthUser() {
        return mPreferencesManager.isToken();
    }

    public void saveAuthUser(String token) {
        mPreferencesManager.saveAuthToken(token);
    }

    public Map<String, String> getUserProfileInfo() {
        Map<String, String> mapUser = new HashMap<>();
        mapUser.put(PreferencesManager.PROFILE_FULL_NAME_KEY, getPreferencesManager().getUserName());
        mapUser.put(PreferencesManager.PROFILE_AVATAR_KEY, getPreferencesManager().getUserAvatar());
        mapUser.put(PreferencesManager.PROFILE_PHONE_KEY, getPreferencesManager().getPhoneUser());
        return mapUser;
    }

    public Map<String, Boolean> getUserSetting() {
        Map<String, Boolean> mapSettings = new HashMap<>();
        mapSettings.put(PreferencesManager.NOTIFICATION_ORDER_KEY, false);
        mapSettings.put(PreferencesManager.NOTIFICATION_PROMO_KEY, false);
        return mapSettings;
    }

    public void saveProfileInfo(String name, String phone, String avatar) {
        getPreferencesManager().saveProfile(name, phone, avatar);
        //getPreferencesManager().saveUserAvatar(avatar);
    }

    public void saveSetting(String notificationPromoKey, boolean isChecked) {

    }

    public UserAddressDTO getAddress(int position) {
        Realm realm = Realm.getDefaultInstance();
        AddressRealm addressRealm = realm.where(AddressRealm.class).equalTo("id", position).findFirst();
        realm.close();
        return new UserAddressDTO(addressRealm);
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public Observable<ProductRealm> getProductFromRealm() {
        return mRealmManager.getAllProductsFromRealm();
    }

    public Observable<AddressRealm> getAddressFromRealm() {
        return mRealmManager.getAllAddressFromRealm();
    }

    public Observable<AvatarUrlRes> uploadUserPhoto(MultipartBody.Part body) {
        return mRestService.uploadUserAvatar(body);
    }
}
