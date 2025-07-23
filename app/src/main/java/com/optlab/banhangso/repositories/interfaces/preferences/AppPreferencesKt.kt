package com.optlab.banhangso.repositories.interfaces.preferences

import com.optlab.banhangso.models.domain.User
import com.optlab.banhangso.models.domain.store.RoleStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface AppPreferencesKt {
    companion object {
        const val PREFS_NAME = "user_preferences"
    }

    /** Sets the layout mode for products using RxJava. */
    fun setLayoutMode(isGrid: Boolean?): Completable

    /** Gets the layout mode for products as a Flowable. */
    fun getLayoutMode(): Flowable<Boolean>

    /** Sets the current store using RxJava. */
    fun setStore(roleStore: RoleStore): Single<Boolean>

    /** Gets the current store as a Flowable. */
    fun getStore(): Flowable<RoleStore>

    /** Sets the current user using RxJava. */
    fun setUser(user: User): Single<Boolean>

    /** Gets the current user as a Flowable. */
    fun getUser(): Flowable<User>

    /** Sets the authentication state using RxJava. */
    fun setIsAuthenticated(state: Boolean): Single<Boolean>

    /** Gets the authentication state as a Flowable. */
    fun isAuthenticated(): Flowable<Boolean>

    /** Sets the store selection state using RxJava. */
    fun setStoreSelected(state: Boolean): Single<Boolean>

    /** Gets the store selection state as a Flowable. */
    fun isStoreSelected(): Flowable<Boolean>

    fun clearStore(): Completable

    /** Clears all user preferences using RxJava. */
    fun clearPreferences(): Completable
}
