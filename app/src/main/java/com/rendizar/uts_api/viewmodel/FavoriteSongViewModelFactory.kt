package com.rendizar.uts_api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rendizar.uts_api.data.db.dao.FavoriteSongDao

/**
 * Factory untuk membuat instance dari FavoriteSongViewModel.
 * Ini diperlukan karena ViewModel kita memiliki constructor dengan parameter (dao).
 */
class FavoriteSongViewModelFactory(private val favoriteSongDao: FavoriteSongDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteSongViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteSongViewModel(favoriteSongDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
