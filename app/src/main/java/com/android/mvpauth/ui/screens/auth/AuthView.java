package com.android.mvpauth.ui.screens.auth;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.mvpauth.R;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.mvp.views.AbstractView;
import com.android.mvpauth.mvp.views.IAuthView;
import com.android.mvpauth.utils.ViewHelper;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Scene;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AuthView extends AbstractView<AuthScreen.AuthPresenter> implements IAuthView {

    public static final int LOGIN_STATE = 0;
    public static final int IDLE_STATE = 1;
    private final Animator scaleAnimator;

    @Inject
    AuthScreen.AuthPresenter mPresenter;

    @BindView(R.id.auth_card)
    CardView mAuthCard;

    @BindView(R.id.show_catalog_btn)
    Button mShowCatalogBtn;
    @BindView(R.id.app_name_tx)
    TextView mAppName;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.vk_img_btn)
    ImageButton mVkImgBtn;
    @BindView(R.id.fb_img_btn)
    ImageButton mFbImgBtn;
    @BindView(R.id.twitter_img_btn)
    ImageButton mTwitterImgBtn;
    @BindView(R.id.login_email_et)
    EditText mLoginEmail;
    @BindView(R.id.login_password_et)
    EditText mLoginPassword;
    @BindView(R.id.login_btn)
    Button mLoginBtn;
    @BindView(R.id.panel_wrapper)
    FrameLayout mPanelWrapper;
    @BindView(R.id.logo_img)
    ImageView mLogo;

    private AuthScreen mScreen;
    private float mDen;
    private final Transition mBounds;
    private final Transition mFade;
    private Subscription animObs;

    /*Handler mHandler = new Handler();

    Runnable animate = new Runnable() {
        @Override
        public void run() {
            Animation scaleAnimation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.scale);
            mVkImgBtn.startAnimation(scaleAnimation);
            mFbImgBtn.startAnimation(scaleAnimation);
            mTwitterImgBtn.startAnimation(scaleAnimation);
            mHandler.postDelayed(animate, 2000);
        }
    };*/

    public AuthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mScreen = Flow.getKey(this);
        }
        mBounds = new ChangeBounds();
        mFade = new Fade();
        mDen = ViewHelper.getDensity(context);

        scaleAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.logo_scale_anim);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<AuthScreen.Component>getDaggerComponent(context).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);

        mAppName.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/PTBebasNeueBook.ttf"));

        if (!isInEditMode()) {
            showViewFromState();
            startLogoAnim();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mPresenter.dropView(this);
            animObs.unsubscribe();
        }
    }

    private void showViewFromState() {
        if (isIdle()) {
            showLoginState();
        } else {
            showIdleState();
        }
    }

    private void showLoginState() {
        CardView.LayoutParams cardParams = (CardView.LayoutParams) mAuthCard.getLayoutParams();
        cardParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        mAuthCard.setLayoutParams(cardParams);
        mAuthCard.getChildAt(0).setVisibility(VISIBLE);
        mAuthCard.setCardElevation(4 * mDen);
        mShowCatalogBtn.setClickable(false);
        mShowCatalogBtn.setVisibility(GONE);
        mScreen.setCustomState(LOGIN_STATE);
    }

    private void showIdleState() {
        CardView.LayoutParams cardParams = (CardView.LayoutParams) mAuthCard.getLayoutParams();
        cardParams.height = ((int) (44 * mDen));
        mAuthCard.setLayoutParams(cardParams);
        mAuthCard.getChildAt(0).setVisibility(INVISIBLE);
        mAuthCard.setCardElevation(0f);
        mShowCatalogBtn.setClickable(true);
        mShowCatalogBtn.setVisibility(VISIBLE);
        mScreen.setCustomState(IDLE_STATE);
    }


    //region ==================== Events ====================
    @OnClick(R.id.login_btn)
    void loginClick() {
        mPresenter.clickOnLogin();
    }

    @OnClick(R.id.show_catalog_btn)
    void showCatalogClick() {
        mPresenter.clickOnShowCatalog();
    }

    @OnClick(R.id.vk_img_btn)
    void vkClick() {
        mPresenter.clickOnVk();
    }

    @OnClick(R.id.fb_img_btn)
    void fbClick() {
        mPresenter.clickOnFb();
    }

    @OnClick(R.id.twitter_img_btn)
    void twitterClick() {
        mPresenter.clickOnTwitter();
    }
    //endregion

    //region ==================== Presenter ====================
    @Override
    public void showLoginBtn() {
        mLoginBtn.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoginBtn() {
        mLoginBtn.setVisibility(GONE);
    }

    @Override
    public void showLoad() {
        mProgressBar.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoad() {
        mProgressBar.setVisibility(GONE);
    }

    @Override
    public void showCatalogScreen() {
        mPresenter.clickOnShowCatalog();
    }

    @Override
    public String getUserEmail() {
        return String.valueOf(mLoginEmail.getText());
    }

    @Override
    public String getUserPassword() {
        return String.valueOf(mLoginPassword.getText());
    }

    @Override
    public boolean isIdle() {
        return mScreen.getCustomState() == IDLE_STATE;
    }

    @Override
    public boolean viewOnBackPressed() {
        if (!isIdle()) {
            showIdleWithAnim();
            return true;
        } else {
            return false;
        }
    }
    //endregion

    @OnClick(R.id.logo_img)
    void test() {
        // TODO: 12.02.2017 start if invalid input variables login or password
        invalidLoginAnimation();
        //showLoginWithAnim();
    }

    //region ==================== Animation ====================
    private void invalidLoginAnimation() {
        //ObjectAnimator
        /*ObjectAnimator oa = ObjectAnimator.ofFloat(mLoginBtn, "rotationY", 2f);
        ObjectAnimator ob = ObjectAnimator.ofFloat(mLoginBtn, "rotationY", -2f);
        ObjectAnimator oc = ObjectAnimator.ofFloat(mLoginBtn, "rotationY", 0f);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(oa, ob, oc);
        set.setDuration(300);
        set.start();*/

        //xml
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.invalid_field_animator);
        set.setTarget(mAuthCard);
        set.start();

    }

    public void showLoginWithAnim() {
        //SCENE
        /*Transition tr = new Slide(Gravity.LEFT);
        FrameLayout root = (FrameLayout) findViewById(R.id.panel_wrapper);
        Scene authScene = Scene.getSceneForLayout(root, R.layout.auth_panel_scene, getContext());
        TransitionManager.go(authScene, tr);*/
        TransitionSet set = new TransitionSet();
        set.addTransition(mBounds)
                .addTransition(mFade)
                .setDuration(300)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        
        TransitionManager.beginDelayedTransition(mPanelWrapper, set);
        showLoginState();

    }

    public void showIdleWithAnim() {
        TransitionSet set = new TransitionSet();

        Transition fade = new Fade();
        fade.addTarget(mAuthCard.getChildAt(0));

        set.addTransition(fade)
                .addTransition(mBounds)
                .addTransition(mFade)
                .setDuration(300)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        TransitionManager.beginDelayedTransition(mPanelWrapper, set);
        showIdleState();
    }

    private void startLogoAnim() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) mLogo.getDrawable();
            scaleAnimator.setTarget(mLogo);

            animObs = Observable.interval(6000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        scaleAnimator.start();
                        avd.start();
                    });

            avd.start();
        }
    }
    //endregion
}
