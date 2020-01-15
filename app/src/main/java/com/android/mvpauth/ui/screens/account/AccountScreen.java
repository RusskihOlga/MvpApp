package com.android.mvpauth.ui.screens.account;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.ActivityResultDto;
import com.android.mvpauth.data.storage.dto.UserAddressDTO;
import com.android.mvpauth.data.storage.dto.UserInfoDto;
import com.android.mvpauth.data.storage.dto.UserSettingDto;
import com.android.mvpauth.data.storage.realm.AddressRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.scopes.AccountScope;
import com.android.mvpauth.flow.AbstractScreen;
import com.android.mvpauth.flow.Screen;
import com.android.mvpauth.mvp.models.AccountModel;
import com.android.mvpauth.mvp.presenters.AbstractPresenter;
import com.android.mvpauth.mvp.presenters.IAccountPresenter;
import com.android.mvpauth.mvp.presenters.RootPresenter;
import com.android.mvpauth.ui.activities.RootActivity;
import com.android.mvpauth.ui.screens.address.AddressScreen;
import com.android.mvpauth.ui.screens.catalog.CatalogScreen;
import com.android.mvpauth.utils.ConstantManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dagger.Provides;
import flow.Flow;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import mortar.MortarScope;
import rx.Observable;
import rx.Subscription;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.text.DateFormat.MEDIUM;

@Screen(R.layout.screen_account)
public class AccountScreen extends AbstractScreen<RootActivity.RootComponent> {

    private int mCustomState = 1;

    public int getCustomState() {
        return mCustomState;
    }

    public void setCustomState(int customState) {
        mCustomState = customState;
    }


    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerAccountScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region ==================== DI ====================

    @dagger.Module
    public class Module {

        @Provides
        @AccountScope
        AccountPresenter provideAccountPresenter() {
            return new AccountPresenter();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @AccountScope
    public interface Component {
        void inject(AccountPresenter presenter);

        void inject(AccountView view);

        RootPresenter getRootPresenter();

        AccountModel getAccountModel();
    }
    //endregion

    //region ==================== Presenter ====================
    public class AccountPresenter extends AbstractPresenter<AccountView, AccountModel> implements IAccountPresenter {

        private File mPhotoFile = null;
        private Subscription mSettingSub;
        private Subscription mActivityResultSub;
        private Subscription mUserInfoSub;
        private Subscription mCameraPhotoSub;
        private Subscription mGalleryPhotoSub;

        //region ======================== life circle ========================

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);

            subscribeOnActivityResultObs();
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if (getView() != null) {
                getView().initView();
            }
            updateListView();
            mCompSubs.add(subscribeOnAddressDtoObs());
            //subscribeOnAddressesObs();
            subscribeOnSettingsObs();
            subscribeOnUserInfoObs();
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle("Личный кабинет")
                    .setBackArrow(false)
                    .build();
        }

        @Override
        protected void initFab() {
            mRootPresenter.newFabBuilder().setVisible(false).build();
        }

        @Override
        protected void onSave(Bundle outState) {
            super.onSave(outState);
            mSettingSub.unsubscribe();
            mUserInfoSub.unsubscribe();
        }

        @Override
        protected void onExitScope() {
            super.onExitScope();
            if (mActivityResultSub != null) mActivityResultSub.unsubscribe();
            if (mCameraPhotoSub != null) mCameraPhotoSub.unsubscribe();
            if (mGalleryPhotoSub != null) mGalleryPhotoSub.unsubscribe();
        }

        //endregion

        //region ======================== subscribtion ========================

        private Subscription subscribeOnAddressDtoObs() {

            return mModel.getAddressObs().subscribe(new ViewSubscriber<AddressRealm>() {
                @Override
                public void onNext(AddressRealm addressRealm) {
                    if (getView() != null) {
                        UserAddressDTO addressDTO = new UserAddressDTO(addressRealm);
                        getView().getAdapter().addItem(addressDTO);
                    }
                }
            });
        }

        private void updateListView() {
            getView().getAdapter().reloadAdapter();
            //subscribeOnAddressesObs();
        }

        private void subscribeOnSettingsObs() {
            mSettingSub = subscribe(mModel.getUserSettingObs(), new ViewSubscriber<UserSettingDto>() {
                @Override
                public void onNext(UserSettingDto userSettingDto) {
                    if (getView() != null) {
                        getView().initSettings(userSettingDto);
                    }
                }
            });
        }

        private void subscribeOnActivityResultObs() {
            Observable<ActivityResultDto> activityResultObs = mRootPresenter.getActivityResultDtoObs()
                    .filter(activityResultDto -> activityResultDto.getResultCode() == Activity.RESULT_OK);

            mActivityResultSub = subscribe(activityResultObs, new ViewSubscriber<ActivityResultDto>() {
                @Override
                public void onNext(ActivityResultDto activityResultDto) {
                    handleActivityResult(activityResultDto);
                }
            });
        }

        private void handleActivityResult(ActivityResultDto activityResultDto) {
            Observable<ActivityResultDto> cameraPhotoObs = Observable.just(activityResultDto)
                    .filter(activityResultDto1 -> activityResultDto1.getRequestCode() == ConstantManager.REQUEST_PROFILE_PHOTO_CAMERA);

            mCameraPhotoSub = subscribe(cameraPhotoObs, new ViewSubscriber<ActivityResultDto>() {
                @Override
                public void onNext(ActivityResultDto activityResultDto) {
                    if (mPhotoFile != null) {
                        getView().updateAvatarPhoto(Uri.fromFile(mPhotoFile));
                    }
                }
            });

            Observable<ActivityResultDto> galleryPhotoObs = Observable.just(activityResultDto)
                    .filter(activityResultDto1 -> activityResultDto1.getRequestCode() == ConstantManager.REQUEST_PROFILE_PHOTO_PICKER);

            mGalleryPhotoSub = subscribe(galleryPhotoObs, new ViewSubscriber<ActivityResultDto>() {
                @Override
                public void onNext(ActivityResultDto activityResultDto) {
                    if (activityResultDto.getIntent() != null) {
                        String photoUrl = activityResultDto.getIntent().getData().toString();
                        getView().updateAvatarPhoto(Uri.parse(photoUrl));
                    }
                }
            });
        }

        private void subscribeOnUserInfoObs() {
            mUserInfoSub = subscribe(mModel.getUserInfoObs(), new ViewSubscriber<UserInfoDto>() {
                @Override
                public void onNext(UserInfoDto userInfoDto) {
                    if (getView() != null) {
                        getView().updateProfileInfo(userInfoDto);
                    }
                }
            });
        }
        //endregion

        @Override
        public void clickOnAddress() {
            Flow.get(getView()).set(new AddressScreen(null));
        }

        @Override
        public void switchViewState() {
            if (getCustomState() == AccountView.EDIT_STATE && getView() != null) {
                mModel.saveProfileInfo(getView().getUserProfileInfo());
            }
            if (getView() != null) {
                getView().changeState();
            }
        }

        @Override
        public void takePhoto() {
            if (getView() != null) {
                getView().showPhotoSourceDialog();
            }
        }

        //region ======================== Camera ========================

        @Override
        public void chooseCamera() {
            if (getRootView() != null) {
                String[] permissions = new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
                if (mRootPresenter.checkPermissionsAndRequestIfNotGranted(permissions,
                        ConstantManager.REQUEST_PERMISSIONS_CAMERA)) {
                    mPhotoFile = createFileForPhoto();
                    if (mPhotoFile == null) {
                        getRootView().showMessage("Фотография не может быть создана");
                        return;
                    }
                    takePhotoFromCamera();
                }
            }
        }

        private void takePhotoFromCamera() {
            //Uri uriForFile = FileProvider.getUriForFile(((RootActivity) getRootView()), ConstantManager.FILE_PROVIDER_AUTHORITY, mPhotoFile);
            Uri uriForFile = Uri.fromFile(mPhotoFile);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
            if (getRootView() != null) {
                ((RootActivity) getRootView()).startActivityForResult(takePictureIntent, ConstantManager.REQUEST_PROFILE_PHOTO_CAMERA);

            }
        }

        private File createFileForPhoto() {
            DateFormat dateTimeInstance = SimpleDateFormat.getTimeInstance(MEDIUM);
            String timeStamp = dateTimeInstance.format(new Date());
            String imageFileName = "IMG_" + timeStamp + "_";
            //File storageDir = getView().getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File fileImage;
            try {
                fileImage = File.createTempFile(imageFileName, ".jpg", storageDir);
            } catch (IOException e) {
                return null;
            }
            return fileImage;
        }
        //endregion

        //region ======================== Gallery ========================
        @Override
        public void chooseGallery() {
            if (getRootView() != null) {
                String[] permissions = new String[]{READ_EXTERNAL_STORAGE};
                if (mRootPresenter.checkPermissionsAndRequestIfNotGranted(permissions, ConstantManager.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)) {
                    takePhotoFromGallery();
                }
            }
        }

        private void takePhotoFromGallery() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT < 19) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            } else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            }
            if (getRootView() != null) {
                ((RootActivity) getRootView()).startActivityForResult(intent, ConstantManager.REQUEST_PROFILE_PHOTO_PICKER);
            }
        }
        //endregion

        @Override
        public void removeAddress(int position) {
            //mModel.removeAddress(position);
            Realm realm = Realm.getDefaultInstance();
            RealmObject entity = realm.where(AddressRealm.class).equalTo("id", position).findFirst();
            if (entity != null) {
                realm.executeTransaction(realm1 -> entity.deleteFromRealm());
                realm.close();
            }
            updateListView();
        }

        @Override
        public void editAddress(int position) {
            Flow.get(getView()).set(new AddressScreen(mModel.getAddressFromId(position++)));
        }

        public void switchSettings() {
            if (getView() != null) {
                mModel.saveSettings(getView().getSettings());
            }
        }
    }


    //endregion
}
