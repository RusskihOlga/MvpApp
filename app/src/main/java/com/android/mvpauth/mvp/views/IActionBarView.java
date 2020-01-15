package com.android.mvpauth.mvp.views;

import android.support.v4.view.ViewPager;

import com.android.mvpauth.mvp.presenters.MenuItemHolder;

import java.util.List;

public interface IActionBarView {
    void setTitleBar(CharSequence title);
    void setVisaible(boolean visible);
    void setBackArrow(boolean enabled);
    void setMenuItem(List<MenuItemHolder> items);
    void setTabLayout(ViewPager pager);
    void removeTabLayout();
}
