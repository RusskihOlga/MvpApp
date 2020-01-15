package com.android.mvpauth.ui.screens.product_details;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.flow.AbstractScreen;
import com.android.mvpauth.ui.screens.product_details.comments.CommentScreen;
import com.android.mvpauth.ui.screens.product_details.descriptions.DescriptionScreen;

import mortar.MortarScope;

public class DetailAdapter extends PagerAdapter {

    private final ProductRealm mProductRealm;

    public DetailAdapter(ProductRealm product) {
        mProductRealm = product;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        AbstractScreen screen = null;
        switch (position) {
            case 0:
                screen = new DescriptionScreen(mProductRealm);
                break;
            case 1:
                screen = new CommentScreen(mProductRealm);
                break;
        }
        MortarScope screenScope = createScreenScopeFromContext(container.getContext(), screen);
        Context screenContext = screenScope.createContext(container.getContext());
        View newView = LayoutInflater.from(screenContext).inflate(screen.getLayoutResId(), container, false);
        container.addView(newView);
        return newView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Описание";
                break;
            case 1:
                title = "Комментарии";
                break;
        }
        return title;
    }

    private MortarScope createScreenScopeFromContext(Context context, AbstractScreen screen) {
        MortarScope parentScope = MortarScope.getScope(context);
        MortarScope childScope = parentScope.findChild(screen.getScopeName());

        if (childScope == null) {
            Object screenComponent = screen.createScreenComponent(parentScope.getService(DaggerService.SERVICE_NAME));
            if (screenComponent == null) {
                throw new IllegalStateException("dont create screen component for " + screen.getScopeName());
            }

            childScope = parentScope.buildChild()
                    .withService(DaggerService.SERVICE_NAME, screenComponent)
                    .build(screen.getScopeName());
        }

        return childScope;
    }
}
