package com.android.mvpauth.data.network.res;

import com.android.mvpauth.data.storage.realm.CommentRealm;
import com.squareup.moshi.Json;

import java.util.Date;

public class CommentRes {
    @Json(name = "_id")
    private String id;
    private int remoteId;
    private String avatar;
    private String userName;
    private float raiting;
    private String commentDate;
    private String comment;
    private boolean active;

    public CommentRes(CommentRealm comment) {
        this.avatar = comment.getAvatar();
        this.userName = comment.getUserName();
        this.raiting = comment.getRaiting();
        this.commentDate = new Date(comment.getCommentDate()).toString();
        this.comment = comment.getComment();
    }

    public String getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUserName() {
        return userName;
    }

    public float getRating() {
        return raiting;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public String getComment() {
        return comment;
    }

    public boolean isActive() {
        return active;
    }
}
