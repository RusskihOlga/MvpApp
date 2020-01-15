package com.android.mvpauth.ui.screens.catalog;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.realm.ProductRealm;

import java.util.ArrayList;
import java.util.List;

import mortar.MortarScope;

public class CatalogAdapter extends PagerAdapter {

    private static final String TAG = "CatalogAdapter";
    private List<ProductRealm> mProductDTOList = new ArrayList<>();

    public CatalogAdapter() {

    }

    @Override
    public int getCount() {
        return mProductDTOList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void addItem(ProductRealm product) {
        mProductDTOList.add(product);
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ProductRealm product = mProductDTOList.get(position);
        Context productContext = CatalogScreen.Factory.createProductContext(product, container.getContext());
        View newView = LayoutInflater.from(productContext).inflate(R.layout.screen_product, container, false);
        newView.setTag("Product" + position);
        container.addView(newView);
        return newView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        MortarScope screenScope = MortarScope.getScope(((View) object).getContext());
        container.removeView((View) object);
        screenScope.destroy();
        Log.e(TAG, "destrouItem with name: " + screenScope.getName());
    }
}
