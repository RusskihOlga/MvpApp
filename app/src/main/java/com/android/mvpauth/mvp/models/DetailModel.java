package com.android.mvpauth.mvp.models;

import com.android.mvpauth.data.storage.realm.CommentRealm;
import com.android.mvpauth.jobs.SendMessageJob;

public class DetailModel extends AbstractModel{

    public void sendComment(String id, CommentRealm commentRealm) {
        SendMessageJob job = new SendMessageJob(id, commentRealm);
        mJobManager.addJobInBackground(job);
    }
}
