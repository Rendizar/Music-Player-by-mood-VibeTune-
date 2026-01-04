package com.rendizar.uts_api

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge // <-- IMPORT BARU UNTUK TEMA MODERN
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rendizar.uts_api.R // Pastikan R diimpor dengan benar

/**
 * Activity utama yang berfungsi sebagai pemilih suasana hati (mood).
 * Tampilan sudah diatur menjadi edge-to-edge (modern).
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Langkah 1: Aktifkan mode edge-to-edge. Harus dipanggil sebelum super.onCreate().
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Langkah 2: Terapkan padding agar konten tidak tertutup oleh status bar atau navigation bar.
        // Pastikan root layout di activity_main.xml memiliki ID 'android:id="@+id/main"'.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menetapkan aksi untuk setiap tombol mood (kode Anda sebelumnya, tidak berubah)
        findViewById<Button>(R.id.btnMoodHappy).setOnClickListener { openPlayer("happy") }
        findViewById<Button>(R.id.btnMoodSad).setOnClickListener { openPlayer("sad") }
        findViewById<Button>(R.id.btnMoodCalm).setOnClickListener { openPlayer("calm") }
        findViewById<Button>(R.id.btnMoodEnergetic).setOnClickListener { openPlayer("energetic") }
    }

    /**
     * Membuka MusicPlayerActivity dan mengirimkan data mood yang dipilih.
     */
    private fun openPlayer(mood: String) {
        val intent = Intent(this, MusicPlayerActivity::class.java).apply {
            putExtra("USER_MOOD", mood)
        }
        startActivity(intent)
    }
}

