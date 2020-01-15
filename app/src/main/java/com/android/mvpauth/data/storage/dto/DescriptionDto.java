package com.android.mvpauth.data.storage.dto;

import com.android.mvpauth.data.storage.realm.ProductRealm;

public class DescriptionDto {

    private String description;
    private float rating;
    private int count;
    private int price;
    private boolean favorite;

    public DescriptionDto(ProductRealm productRealm) {
        this.description = productRealm.getDescription();
        this.rating = productRealm.getRating();
        this.count = productRealm.getCount();
        this.price = productRealm.getPrice();
        this.favorite = productRealm.isFavorite();
    }

    public String getDescription() {
        return description;
    }

    public float getRating() {
        return rating;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
