package com.optlab.banhangso.data.local;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.optlab.banhangso.data.model.app.Resource;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * A generic class that implements the Network Bound Resource pattern to provide data from both
 * local database and network in a reactive way.
 *
 * @param <DatabaseType> Type for the data stored in local database
 * @param <NetworkType> Type for the data returned from API/Firebase
 */
public abstract class NetworkBoundResource<DatabaseType, NetworkType> {

    /**
     * Main entry point that returns a Flowable which emits the resource wrapped data. The flow is:
     * 1. Load data from database 2. Decide if fresh data should be fetched from network 3. If
     * needed, fetch fresh data and save it to database 4. Re-emit data from database
     */
    public Flowable<Resource<DatabaseType>> asFlowable() {
        return Flowable.defer(
                () -> {
                    // First load from database and determine if we need to fetch
                    Flowable<Resource<DatabaseType>> dbSource =
                            loadFromDatabase()
                                    .map(
                                            data -> {
                                                if (shouldFetchFromNetwork(data)) {
                                                    return Resource.loading(data);
                                                } else {
                                                    return Resource.success(data);
                                                }
                                            })
                                    .onErrorReturn(
                                            throwable ->
                                                    Resource.error(throwable.getMessage(), null));

                    // Handle data fetching based on database result
                    return dbSource.flatMap(
                            resource -> {
                                boolean shouldFetchWhenDatabaseEmpty =
                                        resource.data == null
                                                && resource.status != Resource.Status.LOADING
                                                && shouldFetchForEmptyDatabase();

                                boolean shouldFetchStaleData =
                                        resource.status == Resource.Status.SUCCESS
                                                && shouldFetchFromNetwork(resource.data);

                                if (shouldFetchWhenDatabaseEmpty) {
                                    return fetchFromNetwork();
                                } else if (shouldFetchStaleData) {
                                    return fetchFromNetwork(resource.data);
                                } else {
                                    return Flowable.just(resource);
                                }
                            });
                });
    }

    /** Fetch from network with no existing database result */
    private Flowable<Resource<DatabaseType>> fetchFromNetwork() {
        return fetchFromNetwork(null);
    }

    /** Fetch from network with existing database result */
    private Flowable<Resource<DatabaseType>> fetchFromNetwork(DatabaseType dbResult) {
        return fetchFromNetworkSource()
                .subscribeOn(Schedulers.io())
                .flatMapPublisher(
                        networkResponse -> {
                            // Convert network response to database model and save
                            DatabaseType dbModel = mapNetworkResponseToDatabase(networkResponse);
                            saveDatabaseModel(dbModel);

                            // Reload from database to get the fresh data
                            return loadFromDatabase()
                                    .map(Resource::success)
                                    .onErrorReturn(
                                            throwable ->
                                                    Resource.error(
                                                            throwable.getMessage(), dbResult));
                        })
                .onErrorReturn(throwable -> Resource.error(throwable.getMessage(), dbResult))
                .startWithItem(Resource.loading(dbResult));
    }

    /**
     * Determines whether new data should be fetched from network. Typically based on data staleness
     * or emptiness.
     */
    @MainThread
    protected abstract boolean shouldFetchFromNetwork(DatabaseType data);

    /** Determines if we should fetch from network when database returns empty result. */
    @MainThread
    protected boolean shouldFetchForEmptyDatabase() {
        return true;
    }

    /** Loads data from the local database. */
    @NonNull
    @MainThread
    protected abstract Flowable<DatabaseType> loadFromDatabase();

    /** Creates the network API call to fetch fresh data. */
    @NonNull
    @MainThread
    protected abstract Single<NetworkType> fetchFromNetworkSource();

    /**
     * Maps the network response to database model. This is where you convert between your network
     * model and database entity.
     */
    @WorkerThread
    protected abstract DatabaseType mapNetworkResponseToDatabase(NetworkType networkResponse);

    /** Saves the mapped database model to local storage. */
    @WorkerThread
    protected abstract void saveDatabaseModel(DatabaseType item);
}
