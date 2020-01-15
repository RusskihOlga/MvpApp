package com.android.mvpauth.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.data.network.res.CommentRes;
import com.android.mvpauth.data.storage.realm.CommentRealm;
import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import io.realm.Realm;

public class SendMessageJob extends Job {

    private static final String TAG = "SendMessageJob";
    private final String mProductId;
    private final CommentRealm mComment;

    public SendMessageJob(String productId, CommentRealm comment) {
        super(new Params(JobPriority.MID)
                .requireNetwork()
                .persist()
                .groupBy("Comments"));
        mComment = comment;
        mProductId = productId;
    }

    @Override
    public void onAdded() {
        Log.e(TAG, "MESSAGE onAdded: ");
        Realm realm = Realm.getDefaultInstance();
        ProductRealm product = realm.where(ProductRealm.class)
                .equalTo("id", mProductId)
                .findFirst();

        realm.executeTransaction(realm1 -> product.getCommentRealm().add(mComment));
        realm.close();
    }

    @Override
    public void onRun() throws Throwable {
        Log.e(TAG, "MESSAGE onRun: ");

        CommentRes comment = new CommentRes(mComment);
        DataManager.getInstance().sendComment(mProductId, comment)
                .subscribe(commentRes -> {
                    Realm realm = Realm.getDefaultInstance();
                    CommentRealm localComment = realm.where(CommentRealm.class)
                            .equalTo("id", mComment.getId())
                            .findFirst();

                    ProductRealm product = realm.where(ProductRealm.class)
                            .equalTo("id", mProductId)
                            .findFirst();

                    CommentRealm serverComment = new CommentRealm(commentRes);

                    realm.executeTransaction(realm1 -> {
                        localComment.deleteFromRealm();
                        product.getCommentRealm().add(serverComment);
                    });
                    realm.close();
                }, throwable -> {
                    // TODO: 31.01.2017 retry job
                });
    }

    @Override
    protected void onCancel(int i, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int i, int i1) {
        return RetryConstraint.createExponentialBackoff(i, 1000);
    }
}
