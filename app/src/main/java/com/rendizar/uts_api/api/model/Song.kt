package com.rendizar.uts_api.api.model

import com.google.gson.annotations.SerializedName

/**
 * Kelas data yang merepresentasikan lagu dari API.
 */
data class Song(
    // --- PERBAIKAN: Hapus anotasi agar cocok dengan nama variabel ---
    val id: String?,

    val title: String?,
    val artist: String?,

    @SerializedName("artworkUrl100")
    val artworkUrl100: String?,

    @SerializedName("artworkUrl60")
    val artworkUrl60: String?,

    // Properti ini mungkin tidak ada di API, tapi kita biarkan untuk kompatibilitas
    val albumArtUrl: String?,

    @SerializedName("previewUrl")
    val previewUrl: String?
) {
    /**
     * Properti cerdas untuk mendapatkan URL gambar terbaik yang tersedia.
     */
    val bestArtworkUrl: String?
        get() = artworkUrl100?.replace("100x100", "600x600")
            ?: artworkUrl60?.replace("60x60", "600x600")
            ?: albumArtUrl
}
