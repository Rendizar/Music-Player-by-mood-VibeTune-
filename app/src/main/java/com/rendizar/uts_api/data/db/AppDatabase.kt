package com.rendizar.uts_api.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rendizar.uts_api.data.db.dao.FavoriteSongDao
import com.rendizar.uts_api.data.db.entity.FavoriteSong

/**
 * Kelas utama untuk database Room.
 * Versi dinaikkan menjadi 3 karena ada perubahan skema lagi.
 */
@Database(entities = [FavoriteSong::class], version = 3, exportSchema = false) // NAIKKAN VERSI KE 3
public abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteSongDao(): FavoriteSongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vibetune_database"
                )
                // Hapus DB lama jika skema berubah, ini akan mereset favorit
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
