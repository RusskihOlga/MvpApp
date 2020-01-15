package com.android.mvpauth.data.storage.dto;

public class ProductLocalInfo {
    private String remoteId;
    private boolean favorite;
    private int count;

    public ProductLocalInfo() {
    }

    public ProductLocalInfo(String remoteId, boolean favorite, int count) {
        this.remoteId = remoteId;
        this.favorite = favorite;
        this.count = count;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public int getCount() {
        return count;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount() {
        count++;
    }

    public void deleteCount() {
        count--;
    }
}
