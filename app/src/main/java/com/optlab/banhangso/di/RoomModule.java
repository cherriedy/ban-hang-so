package com.optlab.banhangso.di;

import android.content.Context;

import androidx.room.Room;

import com.optlab.banhangso.data.local.dao.StoreDao;
import com.optlab.banhangso.data.local.dao.UserDao;
import com.optlab.banhangso.data.local.database.BanHangSoDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class RoomModule {
    @Provides
    @Singleton
    public static BanHangSoDatabase provideBanHangSoDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, BanHangSoDatabase.class, "banhangso.db")
                .fallbackToDestructiveMigration(true)
                .build();
    }

    @Provides
    @Singleton
    public static StoreDao provideStoreDao(BanHangSoDatabase database) {
        return database.storeDao();
    }

    @Provides
    @Singleton
    public static UserDao provideUserDao(BanHangSoDatabase database) {
        return database.userDao();
    }
}
