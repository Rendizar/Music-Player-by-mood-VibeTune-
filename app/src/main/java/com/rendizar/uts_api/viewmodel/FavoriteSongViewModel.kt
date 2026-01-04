package com.rendizar.uts_api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendizar.uts_api.data.db.dao.FavoriteSongDao
import com.rendizar.uts_api.data.db.entity.FavoriteSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoriteSongViewModel(private val favoriteSongDao: FavoriteSongDao) : ViewModel() {

    // Mendapatkan semua lagu favorit sebagai Flow
    val allFavoriteSongs: Flow<List<FavoriteSong>> = favoriteSongDao.getAll()

    // Mengecek status favorit sebuah lagu sebagai Flow
    fun isFavorite(songId: String): Flow<Boolean> {
        return favoriteSongDao.isFavorite(songId)
    }

    // Menambahkan atau menghapus lagu dari favorit
    fun toggleFavorite(song: FavoriteSong, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                favoriteSongDao.delete(song.id)
            } else {
                favoriteSongDao.insert(song)
            }
        }
    }
}
