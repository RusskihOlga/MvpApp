package com.android.mvpauth.mvp.models;

import com.android.mvpauth.data.storage.realm.ProductRealm;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import rx.Observable;

public class CatalogModel extends AbstractModel {

    public CatalogModel() {
    }

    public boolean isUserAuth() {
        return mDataManager.isAuthUser();
    }

    public void updateBasket(int count) {
        mDataManager.updateCountProductBasket(count);
    }

    public Observable<ProductRealm> getProductObs() {
        Observable<ProductRealm> disk = fromDisk();
        Observable<ProductRealm> network = fromNetwork();

        return Observable.mergeDelayError(disk, network)
                .distinct(ProductRealm::getId);
    }

    public Observable<ProductRealm> fromNetwork() {
        return mDataManager.getProductObsFromNetworkRetry();
    }

    @RxLogObservable
    public Observable<ProductRealm> fromDisk() {
        return mDataManager.getProductFromRealm();
    }
}
