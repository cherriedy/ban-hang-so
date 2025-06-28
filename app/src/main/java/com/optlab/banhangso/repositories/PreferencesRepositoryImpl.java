package com.optlab.banhangso.repositories;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferences;
import com.optlab.banhangso.repositories.interfaces.preferences.PreferencesRepository;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

/**
 * Implementation of PreferencesRepository that provides reactive access to app preferences. This
 * wraps the AppPreferences with RxJava types and manages subjects for each preference key.
 */
public class PreferencesRepositoryImpl implements PreferencesRepository {
    private final AppPreferences appPreferences;

    // Map of preference subjects by key to enable reactive observation
    private final Map<String, Subject<?>> subjectMap = new ConcurrentHashMap<>();

    public PreferencesRepositoryImpl(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;

        // SharedPreferences listener that emits updates to the relevant subjects
        SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener =
                (prefs, key) -> {
                    Subject<?> subject = subjectMap.get(key);
                    if (subject != null) {
                        switch (Objects.requireNonNull(key)) {
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
        return Completable.fromAction(
                () -> {
                    appPreferences.setSortOption(sortOption, key);
                });
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
                AppPreferences.KEY_PRODUCT_LAYOUT_MODE,
                Boolean.class,
                appPreferences::getLayoutMode);
    }

    @Override
    public Completable setStore(@NonNull RoleStore roleStore) {
        return Completable.fromAction(() -> appPreferences.setStore(roleStore));
    }

    @Override
    public Single<RoleStore> getStore() {
        return Single.fromCallable(
                () -> {
                    RoleStore store = appPreferences.getStore();
                    if (store == null) {
                        throw new IllegalStateException("No store found in preferences");
                    }
                    return store;
                });
    }

    @Override
    public Observable<RoleStore> observeStore() {
        return getOrCreateSubject(
                AppPreferences.KEY_CURRENT_STORE, RoleStore.class, appPreferences::getStore);
    }

    @Override
    public Completable clearPreferences() {
        return Completable.fromAction(
                () -> {
                    appPreferences.clearPreferences();
                    // Clear and re-emit all subjects
                    for (Subject<?> subject : subjectMap.values()) {
                        if (!subject.hasComplete()) {
                            // We don't emit null values, instead we just complete the subject
                            subject.onComplete();
                        }
                    }
                    subjectMap.clear();
                });
    }

    @Override
    public Completable setUser(@NonNull User user) {
        return Completable.fromAction(() -> appPreferences.setUser(user));
    }

    @Override
    public Single<User> getUser() {
        return Single.fromCallable(
                () -> {
                    User user = appPreferences.getUser();
                    if (user == null) {
                        throw new IllegalStateException("No user found in preferences");
                    }
                    return user;
                });
    }

    @Override
    public Observable<User> observeUser() {
        return getOrCreateSubject(
                AppPreferences.KEY_CURRENT_USER, User.class, appPreferences::getUser);
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
    private <T> Observable<T> getOrCreateSubject(
            String key, Class<T> type, ValueProvider<T> initialValueProvider) {
        Subject<T> subject = (Subject<T>) subjectMap.get(key);

        if (subject == null) {
            // Create a new subject for this key
            BehaviorSubject<T> newSubject = BehaviorSubject.create();
            subjectMap.put(key, newSubject);

            // Emit initial value if available
            T initialValue = initialValueProvider.get();
            if (initialValue != null) {
                newSubject.onNext(initialValue);
            }

            subject = newSubject;
        }

        return subject;
    }

    /** Specialized helper to create or get a subject for observing SortOption changes. */
    private Observable<SortOption<?>> getOrCreateSortOptionSubject(
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
