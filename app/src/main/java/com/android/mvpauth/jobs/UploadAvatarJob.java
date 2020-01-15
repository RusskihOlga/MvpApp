package com.android.mvpauth.jobs;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.utils.App;
import com.android.mvpauth.utils.ConstantManager;
import com.android.mvpauth.utils.LocalStorageAvatar;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.crashlytics.android.Crashlytics;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UploadAvatarJob extends Job {

    private static final String TAG = "UploadAvatarJob";
    private final String mImageUri;

    public UploadAvatarJob(String imageUri) {
        super(new Params(JobPriority.HIGHT)
                .requireNetwork()
                .persist());
        mImageUri = imageUri;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onAdded() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRun() throws Throwable {
        File file = null;

        try {
            file = new File(LocalStorageAvatar.getPath(App.getContext(), Uri.parse(mImageUri)));
            //file = Compressor.getDefault(App.getContext()).compressToFile(file);
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }

        if(file != null) {
            RequestBody sendFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part mBody = MultipartBody.Part.createFormData("avatar", file.getName(), sendFile);

            DataManager.getInstance()
                    .uploadUserPhoto(mBody)
                    .subscribe(avatarUrlRes -> {
                        DataManager.getInstance().getPreferencesManager().saveUserAvatar(avatarUrlRes.getAvatarUrl());
                    });
        } else {
            Observable.just("Повторите загрузку аватара, произошла ошибка")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(message -> {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    });
        }
        /*Log.e(TAG, "UPLOAD onRun: ");
        File file = new File(Uri.parse(mImageUri).getPath());
        RequestBody sendFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), sendFile);

        DataManager.getInstance().uploadUserPhoto(body).subscribe(avatarUrlRes -> {
            Log.e(TAG, "upload to server: ");
            DataManager.getInstance().getPreferencesManager().saveUserAvatar(avatarUrlRes.getAvatarUrl());
        });*/
    }

    @Override
    protected void onCancel(int i, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int i, int i1) {
        return RetryConstraint.createExponentialBackoff(i, ConstantManager.INITIAL_BACK_OFF_IN_MS);
    }
}
