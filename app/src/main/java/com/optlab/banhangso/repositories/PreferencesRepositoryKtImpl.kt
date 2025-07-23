package com.optlab.banhangso.repositories

import com.optlab.banhangso.models.domain.User
import com.optlab.banhangso.models.domain.store.RoleStore
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferencesKt
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class PreferencesRepositoryKtImpl(private val appPreferenceKt: AppPreferencesKt) :
    PreferencesRepositoryKt {
    override fun getUserRx(): Flowable<User> = appPreferenceKt.getUser()

    override fun setUserRx(user: User): Single<Boolean> = appPreferenceKt.setUser(user)

    override fun getStoreRx(): Flowable<RoleStore> = appPreferenceKt.getStore()

    override fun setStoreRx(roleStore: RoleStore): Single<Boolean> = appPreferenceKt.setStore(roleStore)

    override fun setIsAuthenticatedRx(isAuthenticated: Boolean): Single<Boolean> = appPreferenceKt.setIsAuthenticated(isAuthenticated)

    override fun isAuthenticatedRx(): Flowable<Boolean> = appPreferenceKt.isAuthenticated()

    override fun setStoreSelectedRx(storeSelected: Boolean): Single<Boolean> = appPreferenceKt.setStoreSelected(storeSelected)

    override fun isStoreSelectedRx(): Flowable<Boolean> = appPreferenceKt.isStoreSelected()

    override fun clearStore(): Completable = appPreferenceKt.clearStore()

    override fun clearPreferencesRx(): Completable = appPreferenceKt.clearPreferences()
}
