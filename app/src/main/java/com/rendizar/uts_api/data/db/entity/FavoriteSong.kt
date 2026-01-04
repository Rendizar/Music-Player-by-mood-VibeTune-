package com.rendizar.uts_api.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitas untuk tabel lagu favorit di database.
 */
@Entity(tableName = "favorite_songs")
data class FavoriteSong(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    // --- PERBAIKAN: Gunakan nama kolom yang sama dengan model Song ---
    val albumArtUrl: String?, 
    val previewUrl: String?
)
