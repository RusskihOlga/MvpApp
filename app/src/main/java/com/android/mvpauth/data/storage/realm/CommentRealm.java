package com.android.mvpauth.data.storage.realm;

import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.data.managers.PreferencesManager;
import com.android.mvpauth.data.network.res.CommentRes;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CommentRealm extends RealmObject implements Serializable{

    @PrimaryKey
    private String id;
    private String userName;
    private String avatar;
    private float raiting;
    private long commentDate;
    private String comment;

    public CommentRealm() {
    }

    public CommentRealm(float raiting, String comment) {
        this.id = String.valueOf(this.hashCode());
        final PreferencesManager pm = DataManager.getInstance().getPreferencesManager();
        this.userName = pm.getUserName();
        this.avatar = pm.getUserAvatar();
        this.raiting = raiting;
        this.comment = comment;
        this.commentDate = new Date().getTime();
    }

    public CommentRealm(CommentRes commentRes) {
        this.id = commentRes.getId();
        this.userName = commentRes.getUserName();
        this.avatar = commentRes.getAvatar();
        this.raiting = commentRes.getRating();
        this.comment = commentRes.getComment();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(commentRes.getCommentDate()));
            this.commentDate = c.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public CommentRealm(String userName, String avatar, float raiting, long commentDate, String comment) {
        this.userName = userName;
        this.avatar = avatar;
        this.raiting = raiting;
        this.commentDate = commentDate;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public long getCommentDate() {
        return commentDate;
    }

    public float getRaiting() {
        return raiting;
    }
}
