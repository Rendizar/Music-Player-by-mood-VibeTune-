package com.rendizar.uts_api.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rendizar.uts_api.data.db.entity.FavoriteSong
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) untuk tabel FavoriteSong.
 * Di sinilah kita mendefinisikan semua query database.
 */
@Dao
interface FavoriteSongDao {

    /**
     * Menambahkan lagu ke database.
     * OnConflictStrategy.REPLACE artinya jika lagu dengan ID yang sama sudah ada,
     * data lama akan diganti dengan data baru.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: FavoriteSong)

    /**
     * Menghapus lagu dari database berdasarkan ID-nya.
     */
    @Query("DELETE FROM favorite_songs WHERE id = :songId")
    suspend fun delete(songId: String)

    /**
     * Mengambil semua lagu favorit.
     * Menggunakan Flow agar UI bisa otomatis update saat ada perubahan data.
     */
    @Query("SELECT * FROM favorite_songs")
    fun getAll(): Flow<List<FavoriteSong>>

    /**
     * Mengecek apakah lagu dengan ID tertentu sudah ada di database.
     * Mengembalikan 1 jika ada (true), 0 jika tidak ada (false).
     * Menggunakan Flow agar UI bisa otomatis update status favoritnya.
     */
    @Query("SELECT COUNT(*) FROM favorite_songs WHERE id = :songId")
    fun isFavorite(songId: String): Flow<Boolean>
}
