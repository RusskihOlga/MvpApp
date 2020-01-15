package com.android.mvpauth.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.mvpauth.BuildConfig;
import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.UserInfoDto;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.components.AppComponent;
import com.android.mvpauth.di.modules.PicassoCacheModule;
import com.android.mvpauth.di.modules.RootModule;
import com.android.mvpauth.di.scopes.RootScope;
import com.android.mvpauth.flow.TreeKeyDispatcher;
import com.android.mvpauth.mvp.models.AccountModel;
import com.android.mvpauth.mvp.presenters.MenuItemHolder;
import com.android.mvpauth.mvp.presenters.RootPresenter;
import com.android.mvpauth.mvp.views.IActionBarView;
import com.android.mvpauth.mvp.views.IFabView;
import com.android.mvpauth.mvp.views.IRootView;
import com.android.mvpauth.mvp.views.IView;
import com.android.mvpauth.ui.screens.account.AccountScreen;
import com.android.mvpauth.ui.screens.catalog.CatalogScreen;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import flow.Flow;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class RootActivity extends AppCompatActivity implements IRootView, IActionBarView, IFabView,
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.coordinator_container)
    CoordinatorLayout mCoordinatorContainer;

    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;

    /*@BindView(R.id.count_basket_txt)
    TextView mCountBasket;*/

    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBar;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Inject
    RootPresenter mRootPresenter;
    @Inject
    Picasso mPicasso;
    private ViewPager tabLayout;
    private ActionBarDrawerToggle mToggle;
    private android.support.v7.app.ActionBar mActionBar;
    private List<MenuItemHolder> mActionBarMenuItems;

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = Flow.configure(newBase, this)
                .defaultKey(new CatalogScreen())
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);
        ButterKnife.bind(this);
        RootComponent rootComponent = DaggerService.getDaggerComponent(this);
        rootComponent.inject(this);
        initToolbar();
        mRootPresenter.takeView(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onCreate(outState);
    }

    @Override
    protected void onDestroy() {
        mRootPresenter.dropView(this);
        super.onDestroy();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.setDrawerListener(mToggle);
        mToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (getCurrentScreen() != null && !getCurrentScreen().viewOnBackPressed() && !Flow.get(this).goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Object key = null;
        switch (item.getItemId()) {
            case R.id.nav_account:
                key = new AccountScreen();
                break;
            case R.id.nav_catalog:
                key = new CatalogScreen();
                break;
            case R.id.nav_favorite:
                break;
            case R.id.nav_orders:
                break;
            case R.id.nav_notification:
                break;
        }

        if (key != null) {
            Flow.get(this).set(key);
        }

        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //region  ======================= IRootView  =======================
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
            showMessage(getString(R.string.error));
            // TODO: 21.10.2016 sent error stacktrace to crashlytics
        }
    }

    @Override
    public void startForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showLoad() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoad() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public IView getCurrentScreen() {
        return (IView) mRootFrame.getChildAt(0);
    }

    @Override
    public void initDrawer(UserInfoDto userInfoDto) {
        View header = mNavigationView.getHeaderView(0);
        ImageView avatar = (ImageView) header.findViewById(R.id.drawer_avatar);
        TextView userName = (TextView) header.findViewById(R.id.drawer_user_name);

        mPicasso.load(userInfoDto.getAvatar())
                .fit()
                .centerCrop()
                .into(avatar);

        userName.setText(userInfoDto.getName());
    }

    //endregion

    public void showBasket(int count) {
        //mCountBasket.setText(String.valueOf(count));
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mRootPresenter.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRootPresenter.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    //region  ======================= ActionBar  =======================
    @Override
    public void setTitleBar(CharSequence title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void setVisaible(boolean visible) {
        // TODO: 22.01.2017 implement me
    }

    @Override
    public void setBackArrow(boolean enabled) {
        if (mToggle != null && mActionBar != null) {
            if (enabled) {
                mToggle.setDrawerIndicatorEnabled(false);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                if (mToggle.getToolbarNavigationClickListener() == null) {
                    mToggle.setToolbarNavigationClickListener(v -> onBackPressed());
                }
            } else {
                mActionBar.setDisplayHomeAsUpEnabled(false);
                mToggle.setDrawerIndicatorEnabled(true);
                mToggle.setToolbarNavigationClickListener(null);
            }

            mDrawerLayout.setDrawerLockMode(enabled ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
            mToggle.syncState();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mActionBarMenuItems != null && !mActionBarMenuItems.isEmpty()) {
            for (MenuItemHolder menuItem : mActionBarMenuItems) {
                MenuItem item = menu.add(menuItem.getTitle());
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                        .setIcon(menuItem.getIconResId())
                        .setOnMenuItemClickListener(menuItem.getListener());
            }
        } else {
            menu.close();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setMenuItem(List<MenuItemHolder> items) {
        mActionBarMenuItems = items;
        supportInvalidateOptionsMenu();

    }

    @Override
    public void setTabLayout(ViewPager pager) {
        TabLayout tabView = new TabLayout(this);
        tabView.setupWithViewPager(pager);
        mAppBar.addView(tabView);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabView));
    }

    @Override
    public void removeTabLayout() {
        View tabView = mAppBar.getChildAt(1);
        if (tabView != null && tabView instanceof TabLayout) {
            mAppBar.removeView(tabView);
        }
    }
    //endregion

    //region  ======================= FAB  =======================
    @Override
    public void setVisibleFab(boolean visible) {
        mFab.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setIcon(int icon) {
        mFab.setImageResource(icon);
    }

    @Override
    public void setListener(View.OnClickListener listener) {
        mFab.setOnClickListener(listener);
    }
    //endregion

    //region ======================== DI ========================

    @dagger.Component(dependencies = AppComponent.class, modules = {RootModule.class, PicassoCacheModule.class})
    @RootScope
    public interface RootComponent {
        void inject(RootActivity activity);

        void inject(SplashActivity activity);

        void inject(RootPresenter presenter);

        AccountModel getAccountModel();

        RootPresenter getRootPresenter();

        Picasso getPicasso();
    }

    //endregion
}
