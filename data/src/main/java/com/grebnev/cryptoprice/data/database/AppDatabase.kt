package com.grebnev.cryptoprice.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CoinDbModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var instance: AppDatabase? = null
        private const val DB_NAME = "coin.db"
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            synchronized(LOCK) {
                instance?.let { return it }
                val db =
                    Room
                        .databaseBuilder(
                            context,
                            AppDatabase::class.java,
                            DB_NAME,
                        ).build()
                instance = db
                return db
            }
        }
    }

    abstract fun coinDao(): CoinDao
}