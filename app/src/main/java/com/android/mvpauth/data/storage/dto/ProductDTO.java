package com.android.mvpauth.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.mvpauth.data.network.res.ProductRes;
import com.android.mvpauth.data.storage.realm.ProductRealm;

public class ProductDTO implements Parcelable{

    private int id;
    private String nameProduct;
    private String imageUrl;
    private String description;
    private int price;
    private int count;
    private boolean favorite;

    public ProductDTO(int id, String nameProduct, String imageUrl, String description, int price, int count) {
        this.id = id;
        this.nameProduct = nameProduct;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
        this.count = count;
    }

    //region ====================== Parcelable ======================
    public ProductDTO(Parcel in) {
        id = in.readInt();
        nameProduct = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        price = in.readInt();
        count = in.readInt();
    }

    public ProductDTO(ProductRealm productRealm) {
        this.nameProduct = productRealm.getProductName();
        this.imageUrl = productRealm.getImageUrl();
        this.description = productRealm.getDescription();
        this.price = productRealm.getPrice();
        this.count = productRealm.getCount();
    }

    public ProductDTO(ProductRes productRes, ProductLocalInfo productLocalInfo) {
        id = productRes.getRemoteId();
        nameProduct = productRes.getProductName();
        imageUrl = productRes.getImageUrl();
        description = productRes.getDescription();
        price = productRes.getPrice();
        count = productLocalInfo.getCount();
        favorite = productLocalInfo.isFavorite();
    }

    public static final Creator<ProductDTO> CREATOR = new Creator<ProductDTO>() {
        @Override
        public ProductDTO createFromParcel(Parcel in) {
            return new ProductDTO(in);
        }

        @Override
        public ProductDTO[] newArray(int size) {
            return new ProductDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(nameProduct);
        parcel.writeString(imageUrl);
        parcel.writeString(description);
        parcel.writeInt(price);
        parcel.writeInt(count);
    }
    //endregion

    //region ====================== Getters ======================
    public int getId() {
        return id;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public boolean isFavorite() {
        return favorite;
    }

    //endregion

    public void deleteProduct() {
        count--;
    }

    public void addProduct(){
        count++;
    }
}
