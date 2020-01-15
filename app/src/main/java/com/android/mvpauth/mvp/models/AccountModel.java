package com.android.mvpauth.mvp.models;

import com.android.mvpauth.data.managers.PreferencesManager;
import com.android.mvpauth.data.storage.dto.UserAddressDTO;
import com.android.mvpauth.data.storage.dto.UserInfoDto;
import com.android.mvpauth.data.storage.dto.UserSettingDto;
import com.android.mvpauth.data.storage.realm.AddressRealm;
import com.android.mvpauth.jobs.UploadAvatarJob;

import java.util.ArrayList;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class AccountModel extends AbstractModel{

    private BehaviorSubject<UserInfoDto> mUserInfoObs = BehaviorSubject.create();

    public AccountModel() {
        mUserInfoObs.onNext(getUserProfileInfo());
    }

    //region ==================== Address ====================

    public Observable<AddressRealm> getAddressObs() {
        return mDataManager.getAddressFromRealm();
    }

    public UserAddressDTO getAddressFromId(int position) {
        return mDataManager.getAddress(position);
    }

    //endregion

    //region ==================== Settings ====================

    public Observable<UserSettingDto> getUserSettingObs(){
        return Observable.just(getUserSettings());
    }

    public UserSettingDto getUserSettings() {
        Map<String, Boolean> map = mDataManager.getUserSetting();
        return new UserSettingDto(map.get(PreferencesManager.NOTIFICATION_ORDER_KEY),
                map.get(PreferencesManager.NOTIFICATION_PROMO_KEY));
    }

    public void saveSettings(UserSettingDto settings) {
        mDataManager.saveSetting(PreferencesManager.NOTIFICATION_ORDER_KEY, settings.isOrderNotification());
        mDataManager.saveSetting(PreferencesManager.NOTIFICATION_PROMO_KEY, settings.isPromoNotification());
    }

    //endregion

    //region ==================== user info ====================

    public void saveProfileInfo(UserInfoDto userInfo) {
        mDataManager.saveProfileInfo(userInfo.getName(), userInfo.getPhone(), userInfo.getAvatar());
        mUserInfoObs.onNext(userInfo);

        String uriAvatar = userInfo.getAvatar();
        if (!uriAvatar.contains("http")) {
            uploadAvatarOnServer(uriAvatar);
        }
    }

    private void uploadAvatarOnServer(String uriAvatar) {
        mJobManager.addJobInBackground(new UploadAvatarJob(uriAvatar));
    }

    public UserInfoDto getUserProfileInfo() {
        Map<String, String> map = mDataManager.getUserProfileInfo();
        return new UserInfoDto(map.get(PreferencesManager.PROFILE_FULL_NAME_KEY),
                map.get(PreferencesManager.PROFILE_PHONE_KEY),
                map.get(PreferencesManager.PROFILE_AVATAR_KEY));
    }
    //endregion

    public Observable<UserInfoDto> getUserInfoObs() {
        return mUserInfoObs;
    }


}
