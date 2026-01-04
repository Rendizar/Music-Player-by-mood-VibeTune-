package com.rendizar.uts_api

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rendizar.uts_api.api.ApiClient
import com.rendizar.uts_api.api.adapter.SongAdapter
import com.rendizar.uts_api.api.model.Song
import com.rendizar.uts_api.api.model.SongResponse
import com.rendizar.uts_api.data.db.AppDatabase
import com.rendizar.uts_api.data.db.entity.FavoriteSong
import com.rendizar.uts_api.viewmodel.FavoriteSongViewModel
import com.rendizar.uts_api.viewmodel.FavoriteSongViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MusicPlayerActivity : AppCompatActivity() {

    // Database & ViewModel
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val viewModel: FavoriteSongViewModel by viewModels {
        FavoriteSongViewModelFactory(database.favoriteSongDao())
    }

    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private lateinit var tvCurrentSongTitle: TextView
    private lateinit var tvCurrentSongArtist: TextView
    private lateinit var ivCurrentAlbumArt: ImageView
    private lateinit var btnPlayPause: ImageButton
    private lateinit var tvPlaylistTitle: TextView
    private lateinit var btnShare: ImageButton
    private lateinit var btnFavorite: ImageButton

    // State
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlaylist: List<Song> = emptyList()
    private var currentSongIndex: Int = -1
    private var isCurrentSongFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        initializeViews()
        setupRecyclerView()
        setupClickListeners()

        val playlistType = intent.getStringExtra("PLAYLIST_TYPE") ?: "MOOD"
        val playlistKey = intent.getStringExtra("PLAYLIST_KEY") ?: "calm"

        when (playlistType) {
            "SEARCH" -> {
                tvPlaylistTitle.text = "Hasil pencarian: \"$playlistKey\""
                loadSongsBySearch(playlistKey)
            }
            "FAVORITES" -> {
                tvPlaylistTitle.text = "Lagu Favorit"
                loadFavoriteSongs()
            }
            else -> {
                tvPlaylistTitle.text = "Playlist untuk: ${playlistKey.replaceFirstChar { it.uppercase() }}"
                loadSongsByMood(playlistKey)
            }
        }
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewSongs)
        tvCurrentSongTitle = findViewById(R.id.tvCurrentSongTitle)
        tvCurrentSongArtist = findViewById(R.id.tvCurrentSongArtist)
        ivCurrentAlbumArt = findViewById(R.id.ivCurrentAlbumArt)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        tvPlaylistTitle = findViewById(R.id.tvPlaylistTitle)
        btnShare = findViewById(R.id.btnShare)
        btnFavorite = findViewById(R.id.btnFavorite)
    }

    private fun setupClickListeners() {
        btnPlayPause.setOnClickListener { togglePlayPause() }
        findViewById<ImageButton>(R.id.btnNext).setOnClickListener { playNextSong() }
        findViewById<ImageButton>(R.id.btnPrevious).setOnClickListener { playPreviousSong() }
        btnShare.setOnClickListener { shareCurrentSong() }

        btnFavorite.setOnClickListener {
            if (currentSongIndex != -1) {
                val song = currentPlaylist[currentSongIndex]
                val favoriteSong = song.toFavoriteSong()
                if (favoriteSong != null) {
                    viewModel.toggleFavorite(favoriteSong, isCurrentSongFavorite)
                } else {
                    showToast("Lagu ini tidak bisa difavoritkan (data tidak lengkap atau invalid).")
                }
            }
        }
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter { song ->
            val index = currentPlaylist.indexOf(song)
            if (index != -1) {
                currentSongIndex = index
                playSongAtIndex(currentSongIndex)
            }
        }
        recyclerView.adapter = songAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadSongsByMood(mood: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.songService.getSongsByMood(mood)
                handleApiResponse(response)
            } catch (e: Exception) {
                handleApiError(e)
            }
        }
    }

    private fun loadSongsBySearch(query: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.songService.searchSongs(query)
                handleApiResponse(response)
            } catch (e: Exception) {
                handleApiError(e)
            }
        }
    }

    private fun loadFavoriteSongs() {
        lifecycleScope.launch {
            viewModel.allFavoriteSongs.collectLatest { favoriteSongs ->
                if (favoriteSongs.isNotEmpty()) {
                    currentPlaylist = favoriteSongs.map { it.toSong() }
                    songAdapter.submitList(currentPlaylist)
                    // Reset player jika playlist favorit kosong
                    if(currentPlaylist.isNotEmpty()){
                      currentSongIndex = 0
                      playSongAtIndex(currentSongIndex)
                    } else {
                      showToast("Lagu favorit yang valid tidak ditemukan.")
                      resetPlayerUI()
                    }
                } else {
                    showToast("Belum ada lagu favorit.")
                    resetPlayerUI()
                }
            }
        }
    }

    private fun resetPlayerUI(){
      songAdapter.submitList(emptyList())
      tvCurrentSongTitle.text = "Tidak Ada Lagu"
      tvCurrentSongArtist.text = "Tambahkan lagu ke favoritmu!"
      ivCurrentAlbumArt.setImageResource(R.drawable.ic_launcher_background)
      btnPlayPause.setImageResource(R.drawable.ic_play)
      mediaPlayer?.stop()
      currentPlaylist = emptyList()
      currentSongIndex = -1
    }

    private fun handleApiResponse(response: SongResponse) {
        if (response.success && response.data.isNotEmpty()) {
            currentPlaylist = response.data
            songAdapter.submitList(currentPlaylist)
            currentSongIndex = 0
            playSongAtIndex(currentSongIndex)
        } else {
            showToast("Tidak ada lagu yang ditemukan.")
        }
    }

    private fun handleApiError(e: Exception) {
        showToast("Error: ${e.message}")
        Log.e("SongAPI", "Gagal fetch lagu", e)
    }

    private fun playSongAtIndex(index: Int) {
        if (index < 0 || index >= currentPlaylist.size) return

        val song = currentPlaylist[index]
        updatePlayerUI(song)

        val songId = song.id
        if (!songId.isNullOrBlank()) {
            btnFavorite.isEnabled = true
            observeFavoriteStatus(songId)
        } else {
            isCurrentSongFavorite = false
            btnFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border))
            btnFavorite.isEnabled = false
        }

        mediaPlayer?.release()
        mediaPlayer = null

        val songUrl = song.previewUrl
        if (songUrl.isNullOrBlank() || !songUrl.startsWith("http")) {
            showToast("Error: Link lagu tidak valid.")
            btnPlayPause.setImageResource(R.drawable.ic_play)
            return
        }

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build()
            )
            try {
                setDataSource(songUrl)
                prepareAsync()
            } catch (e: Exception) {
                showToast("Error: Tidak bisa memutar lagu.")
                Log.e("MediaPlayer", "Error setting data source or preparing", e)
                return@apply
            }

            setOnPreparedListener { it.start(); btnPlayPause.setImageResource(R.drawable.ic_pause) }
            setOnCompletionListener { playNextSong() }
        }
    }

    private fun observeFavoriteStatus(songId: String) {
        lifecycleScope.launch {
            viewModel.isFavorite(songId).collectLatest { isFavorite ->
                isCurrentSongFavorite = isFavorite
                val icon = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                btnFavorite.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity, icon))
            }
        }
    }

    private fun shareCurrentSong() {
        if (currentSongIndex == -1 || currentPlaylist.isEmpty()) {
            showToast("Tidak ada lagu yang sedang diputar")
            return
        }
        val song = currentPlaylist[currentSongIndex]
        val shareText = "Aku lagi dengerin \"${song.title}\" oleh ${song.artist} di VibeTune! 🎵"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun updatePlayerUI(song: Song) {
        tvCurrentSongTitle.text = song.title ?: "Judul Tidak Diketahui"
        tvCurrentSongArtist.text = song.artist ?: "Artis Tidak Diketahui"


        val imageUrl = song.bestArtworkUrl
        
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background) 
            .error(R.drawable.ic_launcher_background) 
            .into(ivCurrentAlbumArt)
    }

    private fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                btnPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                it.start()
                btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    private fun playNextSong() {
        if (currentPlaylist.isEmpty()) return
        currentSongIndex = (currentSongIndex + 1) % currentPlaylist.size
        playSongAtIndex(currentSongIndex)
    }

    private fun playPreviousSong() {
        if (currentPlaylist.isEmpty()) return
        currentSongIndex = if (currentSongIndex > 0) currentSongIndex - 1 else currentPlaylist.size - 1
        playSongAtIndex(currentSongIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun Song.toFavoriteSong(): FavoriteSong? {
        val currentId = this.id
        val currentTitle = this.title
        val currentArtist = this.artist
        val currentArtUrl = this.bestArtworkUrl
        val currentPreviewUrl = this.previewUrl

        if (currentId.isNullOrBlank() ||
            currentTitle.isNullOrBlank() ||
            currentArtist.isNullOrBlank() ||
            currentArtUrl.isNullOrBlank() ||
            !currentArtUrl.startsWith("http") ||
            currentPreviewUrl.isNullOrBlank() ||
            !currentPreviewUrl.startsWith("http")) {
            return null
        }
        return FavoriteSong(currentId, currentTitle, currentArtist, currentArtUrl, currentPreviewUrl)
    }

    private fun FavoriteSong.toSong(): Song {

        return Song(id, title, artist, albumArtUrl, null, albumArtUrl, previewUrl)
    }
}
