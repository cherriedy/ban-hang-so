package com.optlab.banhangso.services.interfaces;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.StoreFirebaseObject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

/**
 * @deprecated This class is deprecated and using {@link RenderStoreService} instead.
 */
public interface FirebaseStoreService {
    String STORES_COLLECTION = "stores";

    Single<StoreFirebaseObject> getStore(@NonNull String storeId);

    Single<StoreFirebaseObject> setStore(@NonNull StoreFirebaseObject store);

    Single<List<StoreFirebaseObject>> getUserStores(@NonNull String userId);

    Completable deleteStore(@NonNull String storeId);
}
