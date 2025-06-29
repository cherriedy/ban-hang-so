package com.optlab.banhangso.repositories;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferences;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import timber.log.Timber;

/**
 * Implementation of PreferencesRepository that provides reactive access to app preferences. This
 * wraps the AppPreferences with RxJava types and manages subjects for each preference key.
 */
public class PreferencesRepositoryImpl implements PreferencesRepository {

  private final AppPreferences appPreferences;

  /** Map of preference subjects by key to enable reactive observation */
  private final Map<String, Subject<?>> subjectMap = new ConcurrentHashMap<>();

  /** SharedPreferences listener that emits updates to the relevant subjects */
  private final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;

  public PreferencesRepositoryImpl(@NonNull AppPreferences appPreferences) {
    this.appPreferences = appPreferences;

    onSharedPreferenceChangeListener =
        (prefs, key) -> {
          if (key == null) {
            return;
          }

          Subject<?> subject = subjectMap.get(key);
          if (subject != null) {
            switch (key) {
              case AppPreferences.KEY_PRODUCT_SORT_OPTION,
                      AppPreferences.KEY_BRAND_SORT_OPTION,
                      AppPreferences.KEY_CATEGORY_SORT_OPTION ->
                  emitSortOption(key);
              case AppPreferences.KEY_PRODUCT_LAYOUT_MODE -> emitLayoutMode();
              case AppPreferences.KEY_CURRENT_STORE -> emitStore();
              case AppPreferences.KEY_CURRENT_USER -> emitUser();
            }
          }
        };

    appPreferences.registerPreferencesChangeListener(onSharedPreferenceChangeListener);
  }

  @Override
  public Completable setSortOption(SortOption<? extends Enum<?>> sortOption, String key) {
    return Completable.fromAction(() -> appPreferences.setSortOption(sortOption, key));
  }

  @Override
  public Single<SortOption<?>> getSortOption(String key) {
    return Single.fromCallable(
        () -> {
          SortOption<?> sortOption = appPreferences.getSortOption(key);
          if (sortOption == null) {
            throw new IllegalStateException("Sort option not found for key: " + key);
          }
          return sortOption;
        });
  }

  @Override
  public Observable<SortOption<?>> observeSortOption(String key) {
    return getOrCreateSortOptionSubject(key, () -> appPreferences.getSortOption(key));
  }

  @Override
  public Completable setLayoutMode(Boolean isGrid) {
    return Completable.fromAction(() -> appPreferences.setLayoutMode(isGrid));
  }

  @Override
  public Single<Boolean> getLayoutMode() {
    return Single.fromCallable(appPreferences::getLayoutMode);
  }

  @Override
  public Observable<Boolean> observeLayoutMode() {
    return getOrCreateSubject(
        AppPreferences.KEY_PRODUCT_LAYOUT_MODE, appPreferences::getLayoutMode);
  }

  @Override
  public Completable setStore(@NonNull RoleStore roleStore) {
    return Completable.fromAction(() -> appPreferences.setStore(roleStore));
  }

  @Override
  public Maybe<RoleStore> getStore() {
    return Maybe.fromCallable(appPreferences::getStore);
  }

  @Override
  public Observable<RoleStore> observeStore() {
    return getOrCreateSubject(AppPreferences.KEY_CURRENT_STORE, appPreferences::getStore);
  }

  @Override
  public Completable clearPreferences() {
    return Completable.fromAction(
            () -> {
              appPreferences.clearPreferences();

              // Clear and re-emit all subjects
              for (Subject<?> subject : subjectMap.values()) {
                if (!subject.hasComplete()) {
                  // We do not emit null values, instead we
                  // just complete the subject.
                  subject.onComplete();
                }
              }
              subjectMap.clear();
            })
        .andThen(
            Completable.fromAction(
                () ->
                    appPreferences.unregisterPreferencesChangeListener(
                        onSharedPreferenceChangeListener)));
  }

  @Override
  public Completable setUser(@NonNull User user) {
    return Completable.fromAction(() -> appPreferences.setUser(user));
  }

  @Override
  public Maybe<User> getUser() {
    return Maybe.create(emitter -> {
      User user = appPreferences.getUser();
      if (user == null) {
        emitter.onComplete();
      } else {
        emitter.onSuccess(user);
      }
    });
  }

  @Override
  public Observable<User> observeUser() {
    return getOrCreateSubject(AppPreferences.KEY_CURRENT_USER, appPreferences::getUser);
  }

  /** Helper to emit an updated sort option to its subject. */
  @SuppressWarnings("unchecked")
  private void emitSortOption(String key) {
    Subject<SortOption<?>> subject = (Subject<SortOption<?>>) subjectMap.get(key);
    if (subject != null && !subject.hasComplete()) {
      SortOption<?> value = appPreferences.getSortOption(key);
      if (value != null) {
        subject.onNext(value);
      }
    }
  }

  /** Helper to emit an updated layout mode to its subject. */
  @SuppressWarnings("unchecked")
  private void emitLayoutMode() {
    String key = AppPreferences.KEY_PRODUCT_LAYOUT_MODE;
    Subject<Boolean> subject = (Subject<Boolean>) subjectMap.get(key);
    if (subject != null && !subject.hasComplete()) {
      Boolean value = appPreferences.getLayoutMode();
      subject.onNext(value);
    }
  }

  /** Helper to emit an updated store to its subject. */
  @SuppressWarnings("unchecked")
  private void emitStore() {
    String key = AppPreferences.KEY_CURRENT_STORE;
    Subject<RoleStore> subject = (Subject<RoleStore>) subjectMap.get(key);
    if (subject != null && !subject.hasComplete()) {
      RoleStore value = appPreferences.getStore();
      if (value != null) {
        subject.onNext(value);
      }
    }
  }

  /** Helper to emit an updated user to its subject. */
  @SuppressWarnings("unchecked")
  private void emitUser() {
    String key = AppPreferences.KEY_CURRENT_USER;
    Subject<User> subject = (Subject<User>) subjectMap.get(key);
    if (subject != null && !subject.hasComplete()) {
      User value = appPreferences.getUser();
      if (value != null) {
        subject.onNext(value);
      }
    }
  }

  /** Helper to create or get a subject for observing preference changes. */
  @NonNull @SuppressWarnings("unchecked")
  private <T> Observable<T> getOrCreateSubject(String key, ValueProvider<T> initialValueProvider) {
    Subject<T> subject = (Subject<T>) subjectMap.get(key);

    if (subject == null) {
      Timber.d("Creating new subject for key: %s", key);
      // Create a new subject for this key
      BehaviorSubject<T> newSubject = BehaviorSubject.create();
      subjectMap.put(key, newSubject);

      // Emit initial value if available
      T initialValue = initialValueProvider.get();
      if (initialValue != null) {
        Timber.d("Emitting initial value for key %s: %s", key, initialValue);
        newSubject.onNext(initialValue);
      } else {
        Timber.d("No initial value to emit for key: %s", key);
      }

      subject = newSubject;
    } else {
      Timber.d("Using existing subject for key: %s", key);
    }

    return subject;
  }

  /** Specialized helper to create or get a subject for observing SortOption changes. */
  @NonNull private Observable<SortOption<?>> getOrCreateSortOptionSubject(
      String key, ValueProvider<SortOption<?>> initialValueProvider) {
    Subject<SortOption<?>> subject = (Subject<SortOption<?>>) subjectMap.get(key);

    if (subject == null) {
      // Create a new subject for this key
      BehaviorSubject<SortOption<?>> newSubject = BehaviorSubject.create();
      subjectMap.put(key, newSubject);

      // Emit initial value if available
      SortOption<?> initialValue = initialValueProvider.get();
      if (initialValue != null) {
        newSubject.onNext(initialValue);
      }

      subject = newSubject;
    }

    return subject;
  }

  /** Functional interface for providing initial values for subjects. */
  @FunctionalInterface
  private interface ValueProvider<T> {
    T get();
  }
}
