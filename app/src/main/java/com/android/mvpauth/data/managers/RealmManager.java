package com.android.mvpauth.data.managers;

import com.android.mvpauth.data.network.res.CommentRes;
import com.android.mvpauth.data.network.res.ProductRes;
import com.android.mvpauth.data.storage.realm.AddressRealm;
import com.android.mvpauth.data.storage.realm.CommentRealm;
import com.android.mvpauth.data.storage.realm.ProductRealm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;

public class RealmManager {

    private Realm mRealmInstance;

    public void saveProductResponseToRealm(ProductRes productRes) {
        Realm realm = Realm.getDefaultInstance();

        ProductRealm productRealm = new ProductRealm(productRes);

        if (!productRes.getComments().isEmpty()) {
            Observable.from(productRes.getComments())
                    .doOnNext(commentRes -> {
                        deleteFromRealm(CommentRealm.class, commentRes.getId());
                    }).filter(CommentRes::isActive)
                    .map(CommentRealm::new)
                    .subscribe(commentRealm -> productRealm.getCommentRealm().add(commentRealm));
        }

        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(productRealm));
        realm.close();
    }

    public void deleteFromRealm(Class<? extends RealmObject> entityRealmClass, String id) {
        Realm realm = Realm.getDefaultInstance();
        RealmObject entity = realm.where(entityRealmClass).equalTo("id", id).findFirst();

        if (entity != null) {
            realm.executeTransaction(realm1 -> entity.deleteFromRealm());
            realm.close();
        }
    }

    public Observable<ProductRealm> getAllProductsFromRealm() {
        RealmResults<ProductRealm> managedProduct = getQueryRealmInstance().where(ProductRealm.class).findAllAsync();
        return managedProduct
                .asObservable()
                .filter(RealmResults::isLoaded)
                .flatMap(Observable::from);
    }

    public Observable<AddressRealm> getAllAddressFromRealm() {
        RealmResults<AddressRealm> managedProduct = getQueryRealmInstance().where(AddressRealm.class).findAllAsync();
        return managedProduct
                .asObservable()
                .filter(RealmResults::isLoaded)
                .flatMap(Observable::from);
    }

    private Realm getQueryRealmInstance() {
        if (mRealmInstance == null || mRealmInstance.isClosed()) {
            mRealmInstance = Realm.getDefaultInstance();
        }
        return mRealmInstance;
    }
}
