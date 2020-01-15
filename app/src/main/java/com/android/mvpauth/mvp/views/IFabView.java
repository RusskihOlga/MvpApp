package com.android.mvpauth.mvp.views;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.mvpauth.mvp.presenters.MenuItemHolder;

import java.util.List;

public interface IFabView {
    void setVisibleFab(boolean visible);
    void setIcon(int icon);
    void setListener(View.OnClickListener listener);

}
