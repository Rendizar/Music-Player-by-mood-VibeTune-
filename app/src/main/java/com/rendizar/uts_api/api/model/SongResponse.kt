package com.rendizar.uts_api.api.model

import com.google.gson.annotations.SerializedName

// Updated to match the new API response from your Node.js server with Firestore.
// Example JSON: {"success":true,"mood":"calm","count":6,"data":[...]}
data class SongResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("mood")
    val mood: String? = null,

    @SerializedName("count")
    val count: Int = 0,

    // The list of songs is now inside the "data" key.
    @SerializedName("data")
    val data: List<Song> = emptyList()
)
