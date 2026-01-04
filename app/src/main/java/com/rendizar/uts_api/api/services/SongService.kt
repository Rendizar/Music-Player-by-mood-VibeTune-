package com.rendizar.uts_api.api.services

import com.rendizar.uts_api.api.model.FeaturedResponse
import com.rendizar.uts_api.api.model.SongResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SongService {

    // Updated for Coroutines: added 'suspend' and returns the object directly
    @GET("api/songs/mood/{mood}")
    suspend fun getSongsByMood(@Path("mood") mood: String): SongResponse

    // Updated for Coroutines: added 'suspend' and returns the object directly
    @GET("featured")
    suspend fun getFeaturedContent(): FeaturedResponse

    // --- FUNGSI PENCARIAN BARU ---
    @GET("api/songs/search")
    suspend fun searchSongs(@Query("q") query: String): SongResponse
}
