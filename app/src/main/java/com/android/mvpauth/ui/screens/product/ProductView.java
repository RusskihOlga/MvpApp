package com.android.mvpauth.ui.screens.product;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.mvpauth.R;
import com.android.mvpauth.data.storage.dto.ProductDTO;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.mvp.views.AbstractView;
import com.android.mvpauth.mvp.views.IProductView;
import com.android.mvpauth.utils.ViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.ChangeImageTransform;
import com.transitionseverywhere.Explode;
import com.transitionseverywhere.SidePropagation;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ProductView extends AbstractView<ProductScreen.ProductPresenter> implements IProductView {

    @BindView(R.id.product_name_txt)
    TextView mProductName;
    @BindView(R.id.product_description_txt)
    TextView mProductDescription;
    @BindView(R.id.product_image)
    ImageView mProductImage;
    @BindView(R.id.product_count_txt)
    TextView mProductCount;
    @BindView(R.id.product_price_txt)
    TextView mProductPrice;
    /*@BindView(R.id.plus_btn)
    ImageView mPlusBtn;
    @BindView(R.id.minus_btn)
    ImageView mMinusBtn;*/
    @BindView(R.id.favorite_btn)
    CheckBox favoriteBtn;
    @BindView(R.id.product_wrapper)
    LinearLayout mProductWrapper;
    @BindView(R.id.product_card)
    CardView mProductCard;

    @Inject
    Picasso mPicasso;

    @Inject
    ProductScreen.ProductPresenter mPresenter;
    private AnimatorSet mResultSet;
    private ArrayList<View> mChildsList;
    private int mInImageHeight;
    private float mDen;

    public ProductView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<ProductScreen.Component>getDaggerComponent(context).inject(this);
        mDen = ViewHelper.getDensity(context);
    }

    //region ==================== IProductView ====================

    @Override
    public void showProductView(final ProductDTO product) {
        mProductName.setText(product.getNameProduct());
        mProductDescription.setText(product.getDescription());
        mProductCount.setText(String.valueOf(product.getCount()));
        if (product.getCount() > 0) {
            mProductPrice.setText(String.valueOf(product.getCount() * product.getPrice() + ".-"));
        } else {
            mProductPrice.setText(String.valueOf(product.getPrice() + ".-"));
        }
        favoriteBtn.setChecked(product.isFavorite());

        mPicasso.load(product.getImageUrl())
                .networkPolicy(NetworkPolicy.OFFLINE)
                /*.fit()
                .centerCrop()*/
                .into(mProductImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        mPicasso.load(product.getImageUrl())
                                /*.fit()
                                .centerCrop()*/
                                .into(mProductImage);
                    }
                });
        if (mPresenter.isZoomed()) {
            startZoomTransition();
        }
    }

    @Override
    public void updateProductCountView(ProductDTO productDTO) {
        mProductCount.setText(String.valueOf(productDTO.getCount()));
        if (productDTO.getCount() > 0) {
            mProductPrice.setText(String.valueOf(productDTO.getCount() * productDTO.getPrice() + ".-"));
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        if (mPresenter.isZoomed()) {
            startZoomTransition();
            return true;
        } else
            return false;
    }

    //endregion

    //region ==================== Events ====================

    @OnClick(R.id.plus_btn)
    void ClickPlus() {
        mPresenter.clickOnPlus();
    }

    @OnClick(R.id.minus_btn)
    void ClickMinus() {
        mPresenter.clickOnMinus();
    }

    @OnClick(R.id.favorite_btn)
    void clickOnFavorite() {
        mPresenter.clickFavorite();
    }

    @OnClick(R.id.show_more_btn)
    void clickOnShowMore() {
        mPresenter.clickShowMore();
    }

    @OnClick(R.id.product_image)
    void zoomImage() {
        startZoomTransition();
    }
    //endregion

    //region ==================== Animation ====================
    public void startAddToCardAnim() {
        //animation reveal
        final int cx = (mProductWrapper.getLeft() + mProductWrapper.getRight()) / 2;
        final int cy = (mProductWrapper.getTop() + mProductWrapper.getBottom()) / 2;
        final int radius = Math.max(mProductWrapper.getWidth(), mProductWrapper.getHeight());

        Animator hideCircleAnim;
        Animator showCircleAnim;
        ObjectAnimator hideColorAnim = null;
        ObjectAnimator showColorAnim = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hideCircleAnim = ViewAnimationUtils.createCircularReveal(mProductWrapper, cx, cy, radius, 0);
            hideCircleAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mProductWrapper.setVisibility(INVISIBLE);
                }
            });

            showCircleAnim = ViewAnimationUtils.createCircularReveal(mProductWrapper, cx, cy, 0, radius);
            showCircleAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mProductWrapper.setVisibility(VISIBLE);
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ColorDrawable cdr = (ColorDrawable) mProductWrapper.getForeground();
                hideColorAnim = ObjectAnimator.ofArgb(cdr, "color", getResources().getColor(R.color.color_transparent, null), getResources().getColor(R.color.color_accent, null));
                showColorAnim = ObjectAnimator.ofArgb(cdr, "color", getResources().getColor(R.color.color_accent, null), getResources().getColor(R.color.color_transparent, null));
            }
        } else {
            hideCircleAnim = ObjectAnimator.ofFloat(mProductWrapper, "alpha", 0);
            showCircleAnim = ObjectAnimator.ofFloat(mProductWrapper, "alpha", 1);
        }

        AnimatorSet hideSet = new AnimatorSet();
        AnimatorSet showSet = new AnimatorSet();

        addAnimatorTogetherInSet(hideSet, hideCircleAnim, hideColorAnim);
        addAnimatorTogetherInSet(showSet, showCircleAnim, showColorAnim);
        hideSet.setDuration(600);
        hideSet.setInterpolator(new FastOutSlowInInterpolator());

        //showSet.set(700);
        showSet.setDuration(600);
        showSet.setInterpolator(new FastOutSlowInInterpolator());

        if ((mResultSet != null && !mResultSet.isStarted()) || mResultSet == null) {
            mResultSet = new AnimatorSet();
            mResultSet.playSequentially(hideSet, showSet);
            mResultSet.start();
        }
    }

    private void startZoomTransition() {

        TransitionSet set = new TransitionSet();
        Transition explode = new Explode();

        final Rect rect = new Rect(mProductImage.getLeft(), mProductImage.getTop(), mProductImage.getRight(), mProductImage.getBottom());

        explode.setEpicenterCallback(new Transition.EpicenterCallback() {
            @Override
            public Rect onGetEpicenter(Transition transition) {
                return rect;
            }
        });
        SidePropagation prop = new SidePropagation();
        prop.setPropagationSpeed(4f);
        explode.setPropagation(prop);

        ChangeBounds changeBounds = new ChangeBounds();
        ChangeImageTransform imageTransform = new ChangeImageTransform();

        if (!mPresenter.isZoomed()) {
            changeBounds.setStartDelay(100);
            imageTransform.setStartDelay(100);
        }
        set.addTransition(explode)
                .addTransition(changeBounds)
                .addTransition(imageTransform)
                .setDuration(1000)
                .setInterpolator(new FastOutSlowInInterpolator());

        TransitionManager.beginDelayedTransition(mProductCard, set);

        if (mChildsList == null) {
            mChildsList = ViewHelper.getChildExecudeView(mProductWrapper, R.id.product_image);
        }

        ViewGroup.LayoutParams cardParam = mProductCard.getLayoutParams();
        cardParam.height = !mPresenter.isZoomed() ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        mProductCard.setLayoutParams(cardParam);

        ViewGroup.LayoutParams wrapParam = mProductWrapper.getLayoutParams();
        wrapParam.height = !mPresenter.isZoomed() ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        mProductWrapper.setLayoutParams(wrapParam);
        LinearLayout.LayoutParams imgParam;
        if (!mPresenter.isZoomed()) {
            mInImageHeight = mProductImage.getHeight();
            imgParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mProductImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imgParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mInImageHeight);
            int defMargin = ((int) (16 * mDen));
            imgParam.setMargins(defMargin, 0, defMargin, 0);
            mProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        mProductImage.setLayoutParams(imgParam);

        if (!mPresenter.isZoomed()) {
            for (View view : mChildsList) {
                view.setVisibility(GONE);
            }
        } else {
            for (View view : mChildsList) {
                view.setVisibility(VISIBLE);
            }
        }
        mPresenter.setZoomed(!mPresenter.isZoomed());
    }

    private void addAnimatorTogetherInSet(AnimatorSet set, Animator... anims) {
        ArrayList<Animator> animatorList = new ArrayList<>();
        for (Animator animator : anims) {
            if (animator != null) {
                animatorList.add(animator);
            }
        }
        set.playTogether(animatorList);
    }
    //endregion
}
