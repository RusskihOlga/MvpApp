package com.android.mvpauth.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.android.mvpauth.BuildConfig;
import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.UserInfoDto;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.flow.Screen;
import com.android.mvpauth.flow.TreeKeyDispatcher;
import com.android.mvpauth.mortar.ScreenScoper;
import com.android.mvpauth.mvp.presenters.RootPresenter;
import com.android.mvpauth.mvp.views.IRootView;
import com.android.mvpauth.mvp.views.IView;
import com.android.mvpauth.ui.screens.auth.AuthScreen;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import flow.Flow;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class SplashActivity extends AppCompatActivity implements IRootView {

    @BindView(R.id.coordinator_container)
    CoordinatorLayout mCoordinatorContainer;
    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;

    @Inject
    RootPresenter mRootPresenter;

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = Flow.configure(newBase, this)
                .defaultKey(new AuthScreen())
                .dispatcher(new TreeKeyDispatcher(this))
                .install();

        super.attachBaseContext(newBase);
    }

    @Override
    public Object getSystemService(String name) {
        MortarScope rootActivityScope = MortarScope.findChild(getApplicationContext(), RootActivity.class.getName());
        return rootActivityScope.hasService(name) ? rootActivityScope.getService(name)
                : super.getSystemService(name);
    }

    //region ======================== life circle ========================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        DaggerService.<RootActivity.RootComponent>getDaggerComponent(this).inject(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onCreate(outState);
    }

    @Override
    protected void onStart() {
        mRootPresenter.takeView(this);


        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRootPresenter.dropView(this);
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()){
            ScreenScoper.destroyScreenScope(AuthScreen.class.getName());
        }

        super.onDestroy();
    }

    //endregion

    //region ======================== IAuthView ========================
    @Override
    public void showMessage(String message) {
        Snackbar.make(mCoordinatorContainer, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showError(Throwable e) {
        if (BuildConfig.DEBUG) {
            showMessage(e.getMessage());
            e.printStackTrace();
        } else {
            showMessage("Извините что-то пошло не так, попробуйте позже");
            // TODO: 21.10.2016 sent error stacktrace to crashlytics
        }
    }

    @Override
    public void startForResult(Intent intent, int requestCode) {

    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public void showLoad() {
        //mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoad() {
        //mProgressBar.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public IView getCurrentScreen() {
        return (IView) mRootFrame.getChildAt(0);
    }

    @Override
    public void initDrawer(UserInfoDto userInfoDto) {

    }

    //endregion


    @Override
    public void onBackPressed() {
        if (getCurrentScreen() != null && !getCurrentScreen().viewOnBackPressed() && !Flow.get(this).goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void startRootActivity(){
        Intent intent = new Intent(this, RootActivity.class);
        startActivity(intent);
        finish();
    }
}
