package com.optlab.banhangso.data.local.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;

import com.optlab.banhangso.data.local.dao.StoreDao;
import com.optlab.banhangso.data.local.dao.UserDao;
import com.optlab.banhangso.data.local.entity.BrandEntity;
import com.optlab.banhangso.data.local.entity.CategoryEntity;
import com.optlab.banhangso.data.local.entity.ProductEntity;
import com.optlab.banhangso.data.local.entity.StoreEntity;
import com.optlab.banhangso.data.local.entity.UserEntity;

@Database(
        entities = {
                BrandEntity.class,
                CategoryEntity.class,
                ProductEntity.class,
                StoreEntity.class,
                UserEntity.class
        },
        version = 1,
        exportSchema = false)
public abstract class BanHangSoDatabase extends RoomDatabase {
    public abstract StoreDao storeDao();

    public abstract UserDao userDao();

    /**
     * Clears all tables in the database This is useful when a user logs out to ensure no data
     * leakage between sessions
     */
    @Transaction
    public void clearAllData() {
        clearAllTables(); // Use Room's clearAllTables method
    }
}
