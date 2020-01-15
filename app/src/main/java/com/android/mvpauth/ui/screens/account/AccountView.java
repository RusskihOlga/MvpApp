package com.android.mvpauth.ui.screens.account;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.mvpauth.R;
import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.data.storage.dto.UserDTO;
import com.android.mvpauth.data.storage.dto.UserInfoDto;
import com.android.mvpauth.data.storage.dto.UserSettingDto;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.mvp.views.AbstractView;
import com.android.mvpauth.mvp.views.IAccountView;
import com.android.mvpauth.ui.screens.address.AddressAdapter;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import flow.Flow;

public class AccountView extends AbstractView<AccountScreen.AccountPresenter> implements IAccountView {

    public static final int PREVIEW_STATE = 1;
    public static final int EDIT_STATE = 0;

    @Inject
    Picasso mPicasso;

    @BindView(R.id.profile_name_txt)
    TextView profileNameTxt;
    @BindView(R.id.user_avatar_img)
    CircleImageView userAvatarImg;
    @BindView(R.id.user_phone_et)
    EditText userPhoneEt;
    @BindView(R.id.user_full_name_et)
    EditText userFullNameEt;
    @BindView(R.id.profile_name_wrapper)
    LinearLayout profileNameWrapper;
    @BindView(R.id.address_list)
    RecyclerView addressList;
    @BindView(R.id.add_to_address_btn)
    Button addressBtn;
    @BindView(R.id.notification_order_sw)
    SwitchCompat notificationOrderSw;
    @BindView(R.id.notification_promo_sw)
    SwitchCompat notificationPromoSw;

    private AccountScreen mScreen;
    private TextWatcher mWatcher;
    private AddressAdapter mAdapter;
    private Paint p = new Paint();
    private Uri mAvatarUri;

    public AccountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mScreen = Flow.getKey(this);
        }
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<AccountScreen.Component>getDaggerComponent(context).inject(this);
    }

    public AddressAdapter getAdapter() {
        return mAdapter;
    }

    private void showViewFromState() {
        if (mScreen.getCustomState() == PREVIEW_STATE) {
            showPreviewState();
        } else {
            showEditState();
        }
    }

    public void initView() {
        showViewFromState();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        addressList.setLayoutManager(layoutManager);
        mAdapter = new AddressAdapter();
        addressList.setAdapter(mAdapter);
        initSwipe();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    showRemoveAddressDialog(position);
                } else {
                    shoeEditAddressDialog(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float padding = height / 5;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#ffa815"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + padding / 2, (float) itemView.getTop() + (height - padding) / 2,
                                (float) itemView.getLeft() + 3 * padding / 2, (float) itemView.getBottom() - (height - padding) / 2);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#ffa815"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white_24px);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 3 * padding / 2, (float) itemView.getTop() + (height - padding) / 2,
                                (float) itemView.getRight() - padding / 2, (float) itemView.getBottom() - (height - padding) / 2);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(addressList);
    }

    private void shoeEditAddressDialog(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Перейти к редактированию")
                .setMessage("Вы уверены, что хотите редактировать данный адрес?")
                .setNegativeButton("Редактировать", (dialogInterface, i) -> mPresenter.editAddress(position))
                .setPositiveButton("Отмена", (dialogInterface, i) -> dialogInterface.cancel())
                .setOnCancelListener(dialogInterface -> mAdapter.notifyDataSetChanged())
                .show();
    }

    private void showRemoveAddressDialog(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Удалить адрес?")
                .setMessage("Вы уверены, что хотите удалить адрес из списка?")
                .setNegativeButton("Удалить", (dialogInterface, i) -> mPresenter.removeAddress(position))
                .setPositiveButton("Отмена", (dialogInterface, i) -> dialogInterface.cancel())
                .setOnCancelListener(dialogInterface -> mAdapter.notifyDataSetChanged())
                .show();
    }

    public void initSettings(UserSettingDto settings) {
        CompoundButton.OnCheckedChangeListener listener = (compoundButton, b) -> mPresenter.switchSettings();
        notificationOrderSw.setChecked(settings.isOrderNotification());
        notificationOrderSw.setOnCheckedChangeListener(listener);
        notificationPromoSw.setChecked(settings.isPromoNotification());
        notificationPromoSw.setOnCheckedChangeListener(listener);
    }

    //region ==================== IAccountView ====================
    @Override
    public void changeState() {
        if (mScreen.getCustomState() == PREVIEW_STATE) {
            mScreen.setCustomState(EDIT_STATE);
        } else {
            mScreen.setCustomState(PREVIEW_STATE);
        }
        showViewFromState();
    }

    @Override
    public void showEditState() {
        mWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        profileNameWrapper.setVisibility(VISIBLE);
        userFullNameEt.addTextChangedListener(mWatcher);
        userPhoneEt.setEnabled(true);
        mPicasso.load(R.drawable.ic_add_white_24dp)
                .error(R.drawable.ic_add_white_24dp)
                .into(userAvatarImg);
    }

    @Override
    public void showPreviewState() {
        profileNameWrapper.setVisibility(GONE);
        userPhoneEt.setEnabled(false);
        userFullNameEt.removeTextChangedListener(mWatcher);
        if (mAvatarUri != null) {
            insertAvatar();
        }
    }

    @Override
    public void showPhotoSourceDialog() {
        String source[] = {"Загрузить из галереи", "Сделать фото", "Отмена"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Установить фото");
        alertDialog.setItems(source, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    mPresenter.chooseGallery();
                    break;
                case 1:
                    mPresenter.chooseCamera();
                    break;
                case 2:
                    dialogInterface.cancel();
                    break;
            }
        });
        alertDialog.show();
    }

    @Override
    public String getUserName() {
        return String.valueOf(userFullNameEt.getText());
    }

    @Override
    public String getUserPhone() {
        return String.valueOf(userPhoneEt.getText());
    }

    @Override
    public boolean viewOnBackPressed() {
        if (mScreen.getCustomState() == EDIT_STATE) {
            mPresenter.switchViewState();
            return true;
        } else {
            return false;
        }
    }

    public UserSettingDto getSettings() {
        return new UserSettingDto(notificationOrderSw.isChecked(), notificationPromoSw.isChecked());
    }

    public void updateAvatarPhoto(Uri uri) {
        mAvatarUri = uri;
        DataManager.getInstance().getPreferencesManager().saveUserAvatar(uri.toString());
        insertAvatar();
    }

    private void insertAvatar() {
        mPicasso.load(mAvatarUri)
                .resize(140, 140)
                .centerCrop()
                .into(userAvatarImg);
    }

    public UserInfoDto getUserProfileInfo() {
        return new UserInfoDto(userFullNameEt.getText().toString(), userPhoneEt.getText().toString(), mAvatarUri.toString());
    }

    public void updateProfileInfo(UserInfoDto userInfoDto) {
        profileNameTxt.setText(userInfoDto.getName());
        userFullNameEt.setText(userInfoDto.getName());
        userPhoneEt.setText(userInfoDto.getPhone());
        if (mScreen.getCustomState() == PREVIEW_STATE) {
            mAvatarUri = Uri.parse(userInfoDto.getAvatar());
            insertAvatar();
        }
    }
    //endregion

    //region ==================== Events ====================

    @OnClick(R.id.collapsing_toolbar)
    void testEditMode() {
        mPresenter.switchViewState();
    }

    @OnClick(R.id.add_to_address_btn)
    void ClickAddAddress() {
        mPresenter.clickOnAddress();
    }

    @OnClick(R.id.user_avatar_img)
    void clickChangeAvatar() {
        if (mScreen.getCustomState() == EDIT_STATE) {
            mPresenter.takePhoto();
        }
    }

    //endregion
}
