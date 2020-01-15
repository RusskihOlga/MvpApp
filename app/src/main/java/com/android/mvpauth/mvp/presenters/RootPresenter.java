package com.android.mvpauth.mvp.presenters;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.mvpauth.data.storage.dto.ActivityResultDto;
import com.android.mvpauth.data.storage.dto.UserInfoDto;
import com.android.mvpauth.mvp.models.AccountModel;
import com.android.mvpauth.mvp.views.IRootView;
import com.android.mvpauth.ui.activities.RootActivity;
import com.android.mvpauth.ui.activities.SplashActivity;
import com.android.mvpauth.utils.App;
import com.fernandocejas.frodo.annotation.RxLogSubscriber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mortar.Presenter;
import mortar.bundler.BundleService;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class RootPresenter extends Presenter<IRootView> {

    private PublishSubject<ActivityResultDto> mActivityResultDtoObs = PublishSubject.create();
    @Inject
    AccountModel mAccountModel;
    private Subscription userInfoSub;

    private static int DEFAULT_MODE = 0;
    private static int TAB_MODE = 1;

    public RootPresenter() {
        App.getRootActivityRootComponent().inject(this);
    }

    @Override
    protected BundleService extractBundleService(IRootView view) {
        return (view instanceof RootActivity) ?
                BundleService.getBundleService((RootActivity) view) :
                BundleService.getBundleService((SplashActivity) view);
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (getView() instanceof RootActivity) {
            userInfoSub = subscribeOnUserInfoObs();
        }
    }

    @Override
    public void dropView(IRootView view) {
        if (userInfoSub != null) userInfoSub.unsubscribe();
        super.dropView(view);
    }

    private Subscription subscribeOnUserInfoObs() {
        return mAccountModel.getUserInfoObs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UserInfoSubscriber());
    }

    public PublishSubject<ActivityResultDto> getActivityResultDtoObs() {
        return mActivityResultDtoObs;
    }

    @Nullable
    public IRootView getRootView() {
        return getView();
    }

    public ActionBarBuilder newActionBarBuilder() {
        return new ActionBarBuilder();
    }

    public FabBuilder newFabBuilder() { return new FabBuilder(); }

    @RxLogSubscriber
    private class UserInfoSubscriber extends Subscriber<UserInfoDto> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (getView() != null) {
                getView().showError(e);
            }
        }

        @Override
        public void onNext(UserInfoDto userInfoDto) {
            if (getView() != null) {
                getView().initDrawer(userInfoDto);
            }
        }
    }

    public boolean checkPermissionsAndRequestIfNotGranted(String[] permissions, int requestCode) {
        boolean allGranted = true;
        for (String permission : permissions) {
            if (getView() != null) {
                int selfPermission = ContextCompat.checkSelfPermission(((RootActivity) getView()), permission);
                if (selfPermission != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
        }

        if (!allGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((RootActivity) getView()).requestPermissions(permissions, requestCode);
            }
            return false;
        }
        return allGranted;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mActivityResultDtoObs.onNext(new ActivityResultDto(requestCode, resultCode, intent));
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        // TODO: 24.12.2016 implement me
    }

    public class FabBuilder {
        private boolean isVisible = false;
        private int iconResId;
        private View.OnClickListener listener;

        public FabBuilder setVisible(boolean visible) {
            isVisible = visible;
            return this;
        }

        public FabBuilder setIconResId(int iconResId) {
            this.iconResId = iconResId;
            return this;
        }

        public FabBuilder setListener(View.OnClickListener listener) {
            this.listener = listener;
            return this;
        }

        public void build() {
            if (getView() != null) {
                RootActivity activity = (RootActivity) getView();
                activity.setVisibleFab(isVisible);
                activity.setListener(listener);
                activity.setIcon(iconResId);
            }
        }
    }

    public class ActionBarBuilder {
        private boolean isGoBack = false;
        private boolean isVisible = true;
        private CharSequence title;
        private List<MenuItemHolder> items = new ArrayList<>();
        private ViewPager pager;
        private int toolbarMode = DEFAULT_MODE;

        public ActionBarBuilder setBackArrow(boolean goBack) {
            isGoBack = goBack;
            return this;
        }

        public ActionBarBuilder setVisible(boolean visible) {
            isVisible = visible;
            return this;
        }

        public ActionBarBuilder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public ActionBarBuilder addAction(MenuItemHolder item) {
            items.add(item);
            return this;
        }

        public ActionBarBuilder setTab(ViewPager pager) {
            toolbarMode = TAB_MODE;
            this.pager = pager;
            return this;
        }

        public void build() {
            if (getView() != null) {
                RootActivity activity = (RootActivity) getView();
                activity.setVisaible(isVisible);
                activity.setTitleBar(title);
                activity.setBackArrow(isGoBack);
                activity.setMenuItem(items);
                if (toolbarMode == TAB_MODE) {
                    activity.setTabLayout(pager);
                } else {
                    activity.removeTabLayout();
                }
            }
        }
    }

}
