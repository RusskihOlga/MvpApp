package com.android.mvpauth.data.storage.dto;

import com.android.mvpauth.data.storage.realm.CommentRealm;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommentDto {
    private String userName;
    private String avatarUrl;
    private float rating;
    private String commentDate;
    private String comment;

    public CommentDto(CommentRealm commentRealm) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");
        this.userName = commentRealm.getUserName();
        this.avatarUrl = commentRealm.getAvatar();
        this.rating = commentRealm.getRaiting();
        this.commentDate = df.format(commentRealm.getCommentDate());// commentRealm.getCommentDate();
        this.comment = commentRealm.getComment();
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public float getRating() {
        return rating;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public String getComment() {
        return comment;
    }
}
