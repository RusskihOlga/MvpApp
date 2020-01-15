package com.android.mvpauth.data.network.res;

import com.squareup.moshi.Json;

import java.util.List;

public class ProductRes {
    @Json(name = "_id")
    private String id;
    private int remoteId;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    private float raiting;
    private boolean active;
    private List<CommentRes> comments;

    public String getId() {
        return id;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public String getProductName() {
        return productName;
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

    public float getRaiting() {
        return raiting;
    }

    public boolean isActive() {
        return active;
    }

    public List<CommentRes> getComments() {
        return comments;
    }
}
