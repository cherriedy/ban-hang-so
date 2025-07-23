package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

/**
 * Repository interface that provides reactive access to app preferences. This wraps the
 * AppPreferences with RxJava types for asynchronous and reactive preference handling.
 */
public interface PreferencesRepository {
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
   * @return Maybe that emits the stored store, or completes if not found
   */
  Maybe<RoleStore> getStore();

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
  Maybe<User> getUser();

  /**
   * Observes the current user for continuous updates.
   *
   * @return Observable that emits the stored user whenever it changes
   */
  Observable<User> observeUser();
}
