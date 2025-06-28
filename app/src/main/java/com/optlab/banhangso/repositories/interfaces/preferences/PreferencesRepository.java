package com.optlab.banhangso.repositories.interfaces.preferences;

import androidx.annotation.NonNull;

import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

/**
 * Repository interface that provides reactive access to app preferences.
 * This wraps the AppPreferences with RxJava types for asynchronous and reactive preference handling.
 */
public interface PreferencesRepository {
    /**
     * Sets a sort option in the preferences.
     *
     * @param sortOption The sort option to save
     * @param key The preference key to use
     * @return Completable that completes when the operation is finished
     */
    Completable setSortOption(SortOption<? extends Enum<?>> sortOption, String key);

    /**
     * Gets a sort option from the preferences.
     *
     * @param key The preference key to retrieve
     * @return Single that emits the stored sort option, or errors if not found
     */
    Single<SortOption<?>> getSortOption(String key);

    /**
     * Observes a sort option from the preferences for continuous updates.
     *
     * @param key The preference key to observe
     * @return Observable that emits the stored sort option whenever it changes
     */
    Observable<SortOption<?>> observeSortOption(String key);

    /**
     * Sets the layout mode for products.
     *
     * @param isGrid True for grid layout, false for list layout
     * @return Completable that completes when the operation is finished
     */
    Completable setLayoutMode(Boolean isGrid);

    /**
     * Gets the layout mode for products.
     *
     * @return Single that emits the layout mode value
     */
    Single<Boolean> getLayoutMode();

    /**
     * Observes the layout mode for products for continuous updates.
     *
     * @return Observable that emits the layout mode value whenever it changes
     */
    Observable<Boolean> observeLayoutMode();

    /**
     * Sets the current store.
     *
     * @param roleStore The store to set
     * @return Completable that completes when the operation is finished
     */
    Completable setStore(@NonNull RoleStore roleStore);

    /**
     * Gets the current store.
     *
     * @return Single that emits the stored store, or errors if not found
     */
    Single<RoleStore> getStore();

    /**
     * Observes the current store for continuous updates.
     *
     * @return Observable that emits the stored store whenever it changes
     */
    Observable<RoleStore> observeStore();

    /**
     * Clears all user preferences.
     *
     * @return Completable that completes when the operation is finished
     */
    Completable clearPreferences();

    /**
     * Sets the current user.
     *
     * @param user The user to set
     * @return Completable that completes when the operation is finished
     */
    Completable setUser(@NonNull User user);

    /**
     * Gets the current user.
     *
     * @return Single that emits the stored user, or errors if not found
     */
    Single<User> getUser();

    /**
     * Observes the current user for continuous updates.
     *
     * @return Observable that emits the stored user whenever it changes
     */
    Observable<User> observeUser();
}
