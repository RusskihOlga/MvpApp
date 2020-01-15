package com.android.mvpauth.ui.screens.product_details.descriptions;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.DescriptionDto;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.mvp.views.AbstractView;

import butterknife.BindView;
import butterknife.OnClick;

public class DescriptionView extends AbstractView<DescriptionScreen.DescriptionPresenter>{

    @BindView(R.id.product_description_txt)
    TextView mProductDescription;
    @BindView(R.id.rating)
    AppCompatRatingBar mRating;
    @BindView(R.id.product_count_txt)
    TextView mProductCount;
    @BindView(R.id.product_price_txt)
    TextView mProductPrice;

    public DescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<DescriptionScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public void initView(DescriptionDto descriptionDto) {
        mProductDescription.setText(descriptionDto.getDescription());
        mRating.setRating(descriptionDto.getRating());
        mProductCount.setText(String.valueOf(descriptionDto.getCount()));

        if (descriptionDto.getCount() > 0){
            mProductPrice.setText(String.valueOf(descriptionDto.getCount()*descriptionDto.getPrice() + ".-"));
        } else {
            mProductPrice.setText(String.valueOf(descriptionDto.getPrice() + ".-"));
        }
    }

    @OnClick(R.id.plus_btn)
    void ClickPlusBtn() {
        mPresenter.clickOnPlus();
    }

    @OnClick(R.id.minus_btn)
    void clickOnMinus() {
        mPresenter.clickOnMinus();
    }
}
