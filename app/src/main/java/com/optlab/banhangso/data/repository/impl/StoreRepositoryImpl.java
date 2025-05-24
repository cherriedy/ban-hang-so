package com.optlab.banhangso.data.repository.impl;

import androidx.annotation.NonNull;

import com.optlab.banhangso.data.local.NetworkBoundResource;
import com.optlab.banhangso.data.local.dao.StoreDao;
import com.optlab.banhangso.data.local.entity.StoreEntity;
import com.optlab.banhangso.data.model.Store;
import com.optlab.banhangso.data.model.app.Resource;
import com.optlab.banhangso.data.remote.service.FirebaseStoreService;
import com.optlab.banhangso.data.remote.dto.StoreDto;
import com.optlab.banhangso.data.repository.StoreRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class StoreRepositoryImpl implements StoreRepository {
    private final StoreDao storeDao;
    private final FirebaseStoreService firebaseStoreService;

    @Inject
    public StoreRepositoryImpl(
            StoreDao storeDao,
            FirebaseStoreService firebaseStoreService) {
        this.storeDao = storeDao;
        this.firebaseStoreService = firebaseStoreService;
    }

    /**
     * Get all stores with network bound resource
     */
    @NonNull
    public Flowable<Resource<List<Store>>> getAllStores() {
        return new NetworkBoundResource<List<StoreEntity>, List<StoreDto>>() {
            @Override
            protected boolean shouldFetchFromNetwork(List<StoreEntity> data) {
                // Fetch from network if data is empty or data is older than 30 minutes
                if (data == null || data.isEmpty()) {
                    return true;
                }

                // Check if data is stale (older than 30 minutes)
                Date thirtyMinutesAgo = new Date(
                        System.currentTimeMillis() - 30 * 60 * 1000);
                for (StoreEntity store : data) {
                    if (store.getUpdatedAt() == null
                            || store.getUpdatedAt().before(thirtyMinutesAgo)) {
                        return true;
                    }
                }
                return false;
            }

            @NonNull
            @Override
            protected Flowable<List<StoreEntity>> loadFromDatabase() {
                return storeDao.getAllStores();
            }

            @NonNull
            @Override
            protected Single<List<StoreDto>> fetchFromNetworkSource() {
                return firebaseStoreService.getAllStores();
            }

            @Override
            protected List<StoreEntity> mapNetworkResponseToDatabase(
                    List<StoreDto> items) {
                // Convert DTOs to Room entities
                List<StoreEntity> entities = new ArrayList<>();
                for (StoreDto dto : items) {
                    entities.add(dto.toEntity());
                }
                return entities;
            }

            @Override
            protected void saveDatabaseModel(List<StoreEntity> items) {
                storeDao.insertStores(items).subscribeOn(Schedulers.io()).subscribe();
            }
        }.asFlowable().map(this::mapToDomainStoresResource);
    }

    @NonNull
    private Resource<List<Store>> mapToDomainStoresResource(
            Resource<List<StoreEntity>> resource) {
        if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
            List<Store> domainStores = resource.data
                    .stream()
                    .map(StoreEntity::toDomainModel)
                    .collect(Collectors.toList());
            return Resource.success(domainStores);
        } else if (resource.status == Resource.Status.LOADING) {
            return Resource.loading(
                    resource.data != null ?
                            resource.data
                                    .stream()
                                    .map(StoreEntity::toDomainModel)
                                    .collect(Collectors.toList())
                            : null);
        } else {
            return Resource.error(resource.message, null);
        }
    }

    /**
     * Get a specific store by ID with network bound resource
     */
    @NonNull
    public Flowable<Resource<Store>> getStoreById(@NonNull String storeId) {
        return new NetworkBoundResource<StoreEntity, StoreDto>() {
            @Override
            protected boolean shouldFetchFromNetwork(StoreEntity data) {
                // Fetch from network if data is null or data is older than 30 minutes
                if (data == null) {
                    return true;
                }

                // Check if data is stale
                Date thirtyMinutesAgo = new Date(System.currentTimeMillis() - 30 * 60 * 1000);
                return data.getUpdatedAt() == null || data.getUpdatedAt().before(thirtyMinutesAgo);
            }

            @NonNull
            @Override
            protected Flowable<StoreEntity> loadFromDatabase() {
                return storeDao.getStoreById(storeId).toFlowable();
            }

            @NonNull
            @Override
            protected Single<StoreDto> fetchFromNetworkSource() {
                return firebaseStoreService.getStoreById(storeId);
            }

            @Override
            protected StoreEntity mapNetworkResponseToDatabase(StoreDto item) {
                return item.toEntity();
            }

            @Override
            protected void saveDatabaseModel(StoreEntity item) {
                storeDao.insertStore(item).subscribeOn(Schedulers.io()).subscribe();
            }
        }.asFlowable().map(this::mapToDomainResource);
    }

    /**
     * Save a store to both local database and Firebase
     *
     * <p>This method follows these steps:
     *
     * <ul>
     *   <li>Convert the domain model to a DTO for Firebase
     *   <li>Emit a loading state with the original domain model
     *   <li>Save the DTO to Firebase
     *   <li>Convert the returned DTO to a StoreEntity
     *   <li>Save the StoreEntity to the local database
     *   <li>Map the saved StoreEntity back to a Store domain model
     *   <li>Return the Resource<Store> with the saved domain model
     *   <li>Handle any errors that occur during the process
     *   <li>Log the process for debugging
     *   <li>Return a Flowable<Resource<Store>> that emits the saved store
     *
     * @param domainStore the {@link Store} domain model to be saved
     * @return a {@link Flowable} emitting the {@link Resource} status and saved {@link Store}
     */
    @NonNull
    public Flowable<Resource<Store>> saveStore(@NonNull Store domainStore) {
        Timber.d("saveStore started with domainStore: %s", domainStore);

        // Convert domain model to DTO for Firebase
        StoreDto storeDto = domainStore.toDto();

        // Create a deferred Flowable that will execute when subscribed to
        // defer() - Creates the stream lazily when someone subscribes, not when declared
        return Flowable.defer(() ->
                        // Start by emitting a loading state with the original domain model
                        // startWithItem() - Emits a specific item before starting the main
                        // flow
                        Flowable.just(Resource.loading(domainStore))
                                // Make sure the Flowable runs on the IO scheduler
                                .subscribeOn(Schedulers.io())

                                // Then perform the Firebase save operation
                                .concatWith(
                                        // Save to Firebase using a Single that will emit one item and complete
                                        firebaseStoreService
                                                .saveStore(storeDto)
                                                .doOnSubscribe(d -> Timber.d("Starting Firebase store save"))
                                                .doOnSuccess(s -> Timber.d("Firebase save succeeded: %s", s))

                                                // Convert Single to Flowable and use the saveToDatabase helper method
                                                .flatMapPublisher(this::saveToDatabase)

                                                // Map StoreEntity resource to Store domain model resource
                                                .map(this::mapToDomainResource)

                                                // Handle errors from Firebase and database operations
                                                .onErrorReturn(error -> {
                                                    Timber.e(error, "Error saving store: %s", error.getMessage());
                                                    return Resource.error(error.getMessage(), null);
                                                })
                                ))
                // Log any errors in the outer Flowable
                .doOnError(e -> Timber.e(e, "Unexpected error in saveStore flow"));
    }

    /**
     * Map the Resource<StoreEntity> to Resource<Store>
     *
     * <p>This method converts a Resource<StoreEntity> to Resource<Store>. It checks the status of
     * the Resource and maps the data accordingly.
     *
     * @param entityResource the Resource<StoreEntity> to be mapped
     * @return Resource<Store> with the mapped data
     */
    @NonNull
    private Resource<Store> mapToDomainResource(Resource<StoreEntity> entityResource) {
        if (entityResource.status == Resource.Status.SUCCESS && entityResource.data != null) {
            Store updatedStore = entityResource.data.toDomainModel();
            return Resource.success(updatedStore);
        } else if (entityResource.status == Resource.Status.LOADING && entityResource.data != null) {
            return Resource.loading(entityResource.data.toDomainModel());
        } else {
            return Resource.error(entityResource.message, null);
        }
    }

    /**
     * Save the store to the local database after a successful Firebase save
     *
     * <p>This method is called after a successful save to Firebase. It converts the StoreDto to a
     * StoreEntity and saves it to the local database. It returns a Flowable<Resource<StoreEntity>>
     * that emits the saved StoreEntity wrapped in a Resource object.
     *
     * @param updatedDto the updated StoreDto to be saved to the local database
     * @return a Flowable<Resource<StoreEntity>> that emits the saved StoreEntity wrapped in a
     * Resource
     */
    private Flowable<Resource<StoreEntity>> saveToDatabase(StoreDto updatedDto) {
        StoreEntity updatedEntity = updatedDto.toEntity();
        return storeDao.insertStore(updatedEntity)
                .doOnComplete(
                        () ->
                                Timber.d(
                                        "Local DB save completed for store: %s",
                                        updatedEntity.getId()))
                .doOnError(e -> Timber.e(e, "Local DB save failed"))
                .andThen(Flowable.just(Resource.success(updatedEntity)));
    }

    /**
     * Delete a store from both local database and Firebase
     */
    @NonNull
    public Flowable<Resource<Boolean>> deleteStore(@NonNull String storeId) {
        return Flowable.defer(
                () ->
                        firebaseStoreService
                                .deleteStore(storeId)
                                .flatMapPublisher(
                                        success ->
                                                storeDao.deleteStoreById(storeId)
                                                        .andThen(Flowable.just(Resource.success(true))))
                                .subscribeOn(Schedulers.io())
                                .onErrorReturn(
                                        throwable -> Resource.error(throwable.getMessage(), false))
                                .startWithItem(Resource.loading(false)));
    }
}
