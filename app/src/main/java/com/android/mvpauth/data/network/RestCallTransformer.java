package com.android.mvpauth.data.network;

import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.data.network.error.ErrorUtils;
import com.android.mvpauth.data.network.error.NetworkAvailableError;
import com.android.mvpauth.utils.ConstantManager;
import com.android.mvpauth.utils.NetworkStatusChecker;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import retrofit2.Response;
import rx.Observable;

public class RestCallTransformer<R> implements Observable.Transformer<Response<R>, R> {
    @Override
    @RxLogObservable
    public Observable<R> call(Observable<Response<R>> responseObservable) {

        return NetworkStatusChecker.isInernetAvailable()
                .flatMap(aBoolean -> aBoolean ? responseObservable : Observable.error(new NetworkAvailableError()))
                .flatMap(rResponse -> {
                    switch (rResponse.code()) {
                        case 200:
                            String lastModified = rResponse.headers().get(ConstantManager.LAST_MODIFIED_HEADER);
                            if (lastModified != null) {
                                DataManager.getInstance().getPreferencesManager().saveLastProductUpdate(lastModified);
                            }
                            return Observable.just(rResponse.body());
                        case 304:
                            return Observable.empty();
                        default:
                            return Observable.error(ErrorUtils.parseError(rResponse));
                    }
                });
    }
}
