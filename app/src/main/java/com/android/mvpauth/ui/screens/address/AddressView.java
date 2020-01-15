package com.android.mvpauth.ui.screens.address;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.UserAddressDTO;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.mvp.views.AbstractView;
import com.android.mvpauth.mvp.views.IAddressView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressView extends AbstractView<AddressScreen.AddressPresenter> implements IAddressView {

    @BindView(R.id.edit_a)
    EditText mPlaceEt;
    @BindView(R.id.edit_place)
    EditText mStreetEt;
    @BindView(R.id.edit_house)
    EditText mHouseEt;
    @BindView(R.id.edit_flat)
    EditText mFlatEt;
    @BindView(R.id.edit_floor)
    EditText mFloorEt;
    @BindView(R.id.edit_comment_address)
    EditText mCommentEt;
    @BindView(R.id.add_address_btn)
    Button mAddAddressBtn;

    private int mAddressId;

    public AddressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<AddressScreen.Component>getDaggerComponent(context).inject(this);
    }
    //region ==================== IAddressView ====================

    public void initView(@Nullable UserAddressDTO address) {
        if (address != null){
            mAddressId = address.getId();
            mPlaceEt.setText(address.getName());
            mStreetEt.setText(address.getStreet());
            mHouseEt.setText(address.getHouse());
            mFlatEt.setText(address.getApartment());
            mFloorEt.setText(String.valueOf(address.getFloor()));
            mCommentEt.setText(address.getComment());
            mAddAddressBtn.setText("Сохранить");
        }
    }
    @Override
    public void showInputError() {
        // TODO: 04.12.2016 implement this
    }

    @Override
    public UserAddressDTO getUserAddress() {
        return new UserAddressDTO(mAddressId,
                mPlaceEt.getText().toString(),
                mStreetEt.getText().toString(),
                mHouseEt.getText().toString(),
                mFlatEt.getText().toString(),
                Integer.parseInt(mFloorEt.getText().toString()),
                mCommentEt.getText().toString());
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }
    //endregion

    //region ==================== Events ====================
    @OnClick(R.id.add_address_btn)
    void AddAddress() {
        mPresenter.clickOnAddAddress();
    }
    //endregion
}
