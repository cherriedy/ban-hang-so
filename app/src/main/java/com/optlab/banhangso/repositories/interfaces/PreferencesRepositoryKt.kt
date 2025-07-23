package com.optlab.banhangso.repositories.interfaces

import com.optlab.banhangso.models.domain.User
import com.optlab.banhangso.models.domain.store.RoleStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface PreferencesRepositoryKt {
    /** Get the current user as a Flowable. */
    fun getUserRx(): Flowable<User>

    /** Set the current user using RxJava. */
    fun setUserRx(user: User): Single<Boolean>

    /** Get the current store as an Flowable. */
    fun getStoreRx(): Flowable<RoleStore>

    /** Set the current store using RxJava. */
    fun setStoreRx(roleStore: RoleStore): Single<Boolean>

    /** Set the authentication state using RxJava. */
    fun setIsAuthenticatedRx(isAuthenticated: Boolean): Single<Boolean>

    /** Get the authentication state as an Flowable. */
    fun isAuthenticatedRx(): Flowable<Boolean>

    /** Set the store selection state using RxJava. */
    fun setStoreSelectedRx(storeSelected: Boolean): Single<Boolean>

    /** Get the store selection state as an Flowable. */
    fun isStoreSelectedRx(): Flowable<Boolean>

    fun clearStore(): Completable

    /** Clear the current user and store preferences using RxJava. */
    fun clearPreferencesRx(): Completable
}
