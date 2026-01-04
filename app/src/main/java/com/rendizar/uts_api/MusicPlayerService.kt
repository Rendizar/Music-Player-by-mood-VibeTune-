package com.rendizar.uts_api

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import java.io.IOException

/**
 * Service ini bertanggung jawab untuk memutar musik di latar belakang.
 * Ia menggunakan MediaPlayer untuk mengelola semua logika pemutaran audio.
 * Dengan berjalan sebagai Service, musik dapat terus diputar bahkan saat aplikasi
 * ditutup atau layar ponsel mati.
 */
class MusicPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    // Instance MediaPlayer yang akan digunakan untuk memutar audio.
    // Dibuat nullable agar bisa dilepaskan (release) saat tidak digunakan.
    private var mediaPlayer: MediaPlayer? = null

    // Binder adalah jembatan komunikasi antara Activity (UI) dan Service ini.
    private val binder = MusicBinder()

    /**
     * Inner class yang berfungsi sebagai Binder.
     * Activity akan mendapatkan objek ini dan menggunakannya untuk memanggil
     * metode publik di dalam MusicPlayerService.
     */
    inner class MusicBinder : Binder() {
        // Mengembalikan instance dari Service ini agar Activity bisa mengontrolnya.
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    /**
     * Metode ini dipanggil oleh sistem Android ketika sebuah Activity (misal: MusicPlayerActivity)
     * ingin terhubung (bind) ke Service ini.
     * Ia harus mengembalikan objek IBinder (dalam kasus ini, MusicBinder kita).
     */
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    /**
     * Metode utama untuk memulai pemutaran lagu baru dari sebuah URL.
     * @param url Alamat URL dari file audio (misal: .mp3) yang akan diputar.
     */
    fun playSong(url: String) {
        // Hentikan dan lepaskan MediaPlayer yang sedang berjalan (jika ada)
        // untuk memulai lagu yang baru.
        mediaPlayer?.release()
        mediaPlayer = null

        // Buat instance MediaPlayer baru
        mediaPlayer = MediaPlayer().apply {
            // Atur atribut audio agar sistem tahu kita sedang memutar musik.
            // Ini penting untuk manajemen audio di level sistem (misal: saat ada telepon masuk).
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                // Atur sumber data audio dari URL internet.
                setDataSource(url)
                // Atur listener untuk tahu kapan musik siap diputar.
                setOnPreparedListener(this@MusicPlayerService)
                // Atur listener untuk menangani error jika terjadi.
                setOnErrorListener(this@MusicPlayerService)
                // Memulai persiapan audio secara asynchronous (tidak memblokir thread utama).
                prepareAsync()
            } catch (e: IOException) {
                // Tangani jika URL tidak valid atau ada masalah jaringan awal.
                e.printStackTrace()
            }
        }
    }

    /**
     * Mengalihkan status antara Play dan Pause.
     */
    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.start()
            }
        }
    }

    /**
     * Mengecek apakah musik sedang berputar. Berguna untuk mengubah ikon tombol play/pause di UI.
     * @return true jika musik sedang diputar, false jika tidak.
     */
    fun isPlaying(): Boolean {
        // `?: false` berarti jika mediaPlayer null, kembalikan false.
        return mediaPlayer?.isPlaying ?: false
    }

    /**
     * Dipanggil oleh MediaPlayer ketika proses `prepareAsync()` telah selesai.
     * Ini adalah saat yang tepat untuk memulai pemutaran.
     * @param mp Instance MediaPlayer yang sudah siap.
     */
    override fun onPrepared(mp: MediaPlayer?) {
        // Mulai pemutaran secara otomatis.
        mp?.start()
    }

    /**
     * Dipanggil jika terjadi error selama pemutaran.
     * @return true jika error sudah ditangani, false jika tidak.
     */
    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        // Di sini Anda bisa menambahkan logika penanganan error, misalnya
        // mencoba memutar lagu berikutnya atau menampilkan pesan ke pengguna.
        println("MediaPlayer Error: what=$what, extra=$extra")
        mediaPlayer?.release()
        mediaPlayer = null
        return true // Mengindikasikan bahwa kita sudah menangani error ini.
    }

    /**
     * Dipanggil oleh sistem Android ketika Service akan dihancurkan.
     * Ini adalah kesempatan terakhir untuk membersihkan resource.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Sangat PENTING: Lepaskan resource MediaPlayer untuk menghindari memory leak.
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
