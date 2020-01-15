package com.android.mvpauth.ui.screens.catalog;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.ProductDTO;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.mvp.views.AbstractView;
import com.android.mvpauth.mvp.views.ICatalogView;
import com.android.mvpauth.ui.screens.product.ProductView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class CatalogView extends AbstractView<CatalogScreen.CatalogPresenter> implements ICatalogView {

    @BindView(R.id.add_to_card_btn)
    Button mAddToCardBtn;
    @BindView(R.id.product_pager)
    ViewPager mProductPager;
    @BindView(R.id.indicator)
    CircleIndicator mCircleIndicator;
    private CatalogAdapter mAdapter;

    public CatalogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAdapter = new CatalogAdapter();
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CatalogScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public void showCatalogView() {
        mProductPager.setAdapter(mAdapter);
        mCircleIndicator.setViewPager(mProductPager);
    }

    @Override
    public void updateProductCounter() {
        // TODO: 29.10.2016 update count product on cart icon
    }

    public int getCurrentPagerPosition() {
        return mProductPager.getCurrentItem();
    }

    @Override
    public boolean viewOnBackPressed() {
        return getCurrentProductView().viewOnBackPressed();
    }

    @OnClick(R.id.add_to_card_btn)
    void clickAddToCard() {
        mPresenter.clickOnBuyButton(mProductPager.getCurrentItem());
    }

    public CatalogAdapter getAdapter() {
        return mAdapter;
    }

    public ProductView getCurrentProductView() {
        return (ProductView) mProductPager.findViewWithTag("Product" + mProductPager.getCurrentItem());
    }
}
