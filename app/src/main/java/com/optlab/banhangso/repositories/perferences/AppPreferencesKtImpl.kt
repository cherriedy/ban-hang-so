package com.optlab.banhangso.repositories.perferences

import android.content.Context
import androidx.datastore.rxjava3.RxDataStore
import androidx.datastore.rxjava3.RxDataStoreBuilder
import com.optlab.banhangso.datastore.UserPreferences
import com.optlab.banhangso.models.domain.User
import com.optlab.banhangso.models.domain.store.RoleStore
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferencesKt
import com.optlab.banhangso.repositories.perferences.ProtoObjectsMapper.toDomain
import com.optlab.banhangso.repositories.perferences.ProtoObjectsMapper.toProto
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class AppPreferencesKtImpl(private val context: Context) : AppPreferencesKt {
    private val rxDataStore: RxDataStore<UserPreferences> by lazy {
        RxDataStoreBuilder(context, AppPreferencesKt.PREFS_NAME, AppPreferencesSerializer).build()
    }

    private val disposables = CompositeDisposable()

    init {
        val disposable =
            rxDataStore.data()
                .take(1)
                .subscribe(
                    { prefs ->
                        Timber.d("All preferences:")
                        Timber.d("  User: ${prefs.user}")
                        Timber.d("  Store: ${prefs.store}")
                        Timber.d("  isAuthenticated: ${prefs.isAuthenticated}")
                        Timber.d("  isStoreSelected: ${prefs.isStoreSelected}")
                        Timber.d("  layoutModeIsGrid: ${prefs.layoutModeIsGrid}")
                    },
                    { e -> Timber.e(e, "Error logging preferences") },
                )
        disposables.add(disposable)
    }

    override fun setLayoutMode(isGrid: Boolean?): Completable =
        rxDataStore.updateDataAsync { currentPreferences ->
            Single.just(currentPreferences.toBuilder().setLayoutModeIsGrid(isGrid ?: false).build())
        }.ignoreElement()
            .doOnComplete {
                Timber.d("Layout mode set to: ${isGrid ?: false}")
            }

    override fun getLayoutMode(): Flowable<Boolean> =
        rxDataStore.data()
            .map { it.layoutModeIsGrid }
            .distinctUntilChanged()

    override fun setStore(roleStore: RoleStore): Single<Boolean> =
        rxDataStore.updateDataAsync { currentPreferences ->
            Single.just(currentPreferences.toBuilder().setStore(roleStore.toProto()).build())
        }.ignoreElement()
            .toSingleDefault(true)
            .doOnSuccess {
                Timber.d("Store set successfully: %s", roleStore)
            }
            .onErrorReturnItem(false)

    override fun getStore(): Flowable<RoleStore> =
        rxDataStore.data()
            .map { it.store.toDomain() }
            .distinctUntilChanged()

    override fun setUser(user: User): Single<Boolean> =
        rxDataStore.updateDataAsync { currentPreferences ->
            Single.just(currentPreferences.toBuilder().setUser(user.toProto()).build())
        }.ignoreElement()
            .toSingleDefault(true)
            .doOnSuccess {
                Timber.d("User set successfully: %s", user)
            }
            .onErrorReturnItem(false)

    override fun getUser(): Flowable<User> =
        rxDataStore.data()
            .map { it.user.toDomain() }
            .distinctUntilChanged()

    override fun setIsAuthenticated(state: Boolean): Single<Boolean> =
        rxDataStore.updateDataAsync { currentPreferences ->
            Single.just(currentPreferences.toBuilder().setIsAuthenticated(state).build())
        }.ignoreElement()
            .toSingleDefault(true)
            .doOnSuccess {
                Timber.d("Authentication state set to: %s", state)
            }
            .onErrorReturnItem(false)

    override fun isAuthenticated(): Flowable<Boolean> =
        rxDataStore.data()
            .map { it.isAuthenticated }
            .distinctUntilChanged()

    override fun setStoreSelected(state: Boolean): Single<Boolean> =
        rxDataStore.updateDataAsync { currentPreferences ->
            Single.just(currentPreferences.toBuilder().setIsStoreSelected(state).build())
        }.ignoreElement()
            .toSingleDefault(true)
            .doOnSuccess {
                Timber.d("Store selection state set to: %s", state)
            }
            .onErrorReturnItem(false)

    override fun isStoreSelected(): Flowable<Boolean> =
        rxDataStore.data()
            .map { it.isStoreSelected }
            .distinctUntilChanged()

    override fun clearStore(): Completable =
        rxDataStore.updateDataAsync { currentPreferences ->
            Single.just(
                currentPreferences.toBuilder()
                    .clearStore()
                    .clearIsStoreSelected()
                    .build(),
            )
        }.ignoreElement()
            .doOnComplete {
                Timber.d("Store cleared successfully")
            }

    override fun clearPreferences(): Completable =
        rxDataStore.updateDataAsync {
            Single.just(
                UserPreferences.newBuilder()
                    .clearUser()
                    .clearStore()
                    .clearIsAuthenticated()
                    .clearIsStoreSelected()
                    .clearLayoutModeIsGrid()
                    .build(),
            )
        }.ignoreElement()
            .doOnComplete {
                Timber.d("User preferences cleared successfully")
            }
}
