package com.android.mvpauth.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.mvpauth.data.managers.PreferencesManager;

import java.util.ArrayList;
import java.util.Map;

public class UserDTO implements Parcelable{
    private int id;
    private String fullName;
    private String avatar;
    private String phone;
    private boolean orderNotification;
    private boolean promoNotification;
    private ArrayList<UserAddressDTO> userAddress;

    protected UserDTO(Parcel in) {
        id = in.readInt();
        fullName = in.readString();
        avatar = in.readString();
        phone = in.readString();
        orderNotification = in.readByte() != 0;
        promoNotification = in.readByte() != 0;
        userAddress = in.createTypedArrayList(UserAddressDTO.CREATOR);
    }

    public UserDTO(Map<String, String> userProfileInfo, ArrayList<UserAddressDTO> userAddress, Map<String, Boolean> userSettings) {
        this.fullName = userProfileInfo.get(PreferencesManager.PROFILE_FULL_NAME_KEY);
        this.avatar = userProfileInfo.get(PreferencesManager.PROFILE_AVATAR_KEY);
        this.phone = userProfileInfo.get(PreferencesManager.PROFILE_PHONE_KEY);
        this.orderNotification = userSettings.get(PreferencesManager.NOTIFICATION_ORDER_KEY);
        this.promoNotification = userSettings.get(PreferencesManager.NOTIFICATION_PROMO_KEY);
        this.userAddress = userAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isOrderNotification() {
        return orderNotification;
    }

    public void setOrderNotification(boolean orderNotification) {
        this.orderNotification = orderNotification;
    }

    public boolean isPromoNotification() {
        return promoNotification;
    }

    public void setPromoNotification(boolean promoNotification) {
        this.promoNotification = promoNotification;
    }

    public ArrayList<UserAddressDTO> getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(ArrayList<UserAddressDTO> userAddress) {
        this.userAddress = userAddress;
    }

    public static final Creator<UserDTO> CREATOR = new Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(fullName);
        parcel.writeString(avatar);
        parcel.writeString(phone);
        parcel.writeByte((byte) (orderNotification ? 1 : 0));
        parcel.writeByte((byte) (promoNotification ? 1 : 0));
        parcel.writeTypedList(userAddress);
    }
}
