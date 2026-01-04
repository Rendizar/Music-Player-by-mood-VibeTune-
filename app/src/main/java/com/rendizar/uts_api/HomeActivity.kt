package com.rendizar.uts_api

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.rendizar.uts_api.api.ApiClient
import com.rendizar.uts_api.api.model.MixItem
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    // Views
    private lateinit var etCurhat: TextInputEditText
    private lateinit var btnAnalyze: MaterialButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var chipHappy: Chip
    private lateinit var chipSad: Chip
    private lateinit var chipEnergetic: Chip
    private lateinit var chipCalm: Chip
    private lateinit var btnToggleInput: ImageButton
    private lateinit var inputContentGroup: LinearLayout
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var inputHeader: RelativeLayout
    private lateinit var cardInput: MaterialCardView

    // State
    private var isInputBoxExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        initializeViews()
        loadFeaturedContent()
        setupClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showSearchDialog()
                true
            }
            R.id.action_favorites -> {
                navigateToFavorites()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeViews() {
        rootLayout = findViewById(R.id.root_layout_home)
        etCurhat = findViewById(R.id.etCurhat)
        btnAnalyze = findViewById(R.id.btnAnalyzeMood)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        chipHappy = findViewById(R.id.chipHappy)
        chipSad = findViewById(R.id.chipSad)
        chipEnergetic = findViewById(R.id.chipEnergetic)
        chipCalm = findViewById(R.id.chipCalm)
        btnToggleInput = findViewById(R.id.btnToggleInput)
        inputContentGroup = findViewById(R.id.inputContentGroup)
        inputHeader = findViewById(R.id.inputHeader)
        cardInput = findViewById(R.id.cardInput)
    }

    private fun setupClickListeners() {
        swipeRefreshLayout.setOnRefreshListener { loadFeaturedContent() }

        inputHeader.setOnClickListener { 
            isInputBoxExpanded = !isInputBoxExpanded
            toggleInputBox(isInputBoxExpanded)
        }

        btnAnalyze.setOnClickListener {
            val curhatan = etCurhat.text.toString().trim()
            if (curhatan.isEmpty()) {
                Toast.makeText(this, "Tolong ketik dulu perasaanmu...", Toast.LENGTH_SHORT).show()
            } else {
                analyzeMoodAndPlayMusic(curhatan)
            }
        }

        chipHappy.setOnClickListener { navigateToMusicPlayerWithMood("happy", "😄") }
        chipSad.setOnClickListener { navigateToMusicPlayerWithMood("sad", "😢") }
        chipEnergetic.setOnClickListener { navigateToMusicPlayerWithMood("energetic", "🔥") }
        chipCalm.setOnClickListener { navigateToMusicPlayerWithMood("calm", "☕") }
    }

    private fun toggleInputBox(show: Boolean) {
        TransitionManager.beginDelayedTransition(cardInput)
        if (show) {
            inputContentGroup.visibility = View.VISIBLE
            btnToggleInput.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_up))
        } else {
            inputContentGroup.visibility = View.GONE
            btnToggleInput.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_down))
        }
    }

    private fun loadFeaturedContent() {
        lifecycleScope.launch {
            try {
                swipeRefreshLayout.isRefreshing = true
                val data = ApiClient.songService.getFeaturedContent()

                findViewById<TextView>(R.id.txtBannerTitle).text = data.banner.title
                findViewById<TextView>(R.id.txtBannerDesc).text = data.banner.description
                Glide.with(this@HomeActivity).load(data.banner.imageUrl).into(findViewById(R.id.imgBanner))

                val mixes = data.topMixes
                if (mixes.isNotEmpty()) updateGridItem(R.id.imgGrid1, R.id.txtGrid1, mixes[0])
                if (mixes.size >= 2) updateGridItem(R.id.imgGrid2, R.id.txtGrid2, mixes[1])
                if (mixes.size >= 3) updateGridItem(R.id.imgGrid3, R.id.txtGrid3, mixes[2])
                if (mixes.size >= 4) updateGridItem(R.id.imgGrid4, R.id.txtGrid4, mixes[3])

            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Gagal memuat: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun updateGridItem(imgId: Int, txtId: Int, item: MixItem) {
        val img = findViewById<ImageView>(imgId)
        val txt = findViewById<TextView>(txtId)
        txt.text = item.title
        Glide.with(this).load(item.imageUrl).transform(CenterCrop(), RoundedCorners(8)).into(img)
    }

    private fun analyzeMoodAndPlayMusic(text: String) {
        val lowerText = text.lowercase()
        var detectedMood = "calm"
        var emoji = "☕"

        if (containsKeyword(lowerText, listOf("senang", "bahagia", "happy", "asik"))) {
            detectedMood = "happy"
            emoji = "🎉"
        } else if (containsKeyword(lowerText, listOf("sedih", "nangis", "kecewa", "galau"))) {
            detectedMood = "sad"
            emoji = "☔"
        } else if (containsKeyword(lowerText, listOf("semangat", "energi", "lari", "gym"))) {
            detectedMood = "energetic"
            emoji = "🔥"
        }
        navigateToMusicPlayerWithMood(detectedMood, emoji)
    }

    private fun showSearchDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cari Lagu atau Artis")

        val input = EditText(this)
        input.hint = "Ketik di sini..."
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(48, 0, 48, 0)
        input.layoutParams = lp
        container.addView(input)
        builder.setView(container)

        builder.setPositiveButton("Cari") { dialog, _ ->
            val query = input.text.toString().trim()
            if (query.isNotEmpty()) {
                navigateToMusicPlayerWithSearch(query)
            } else {
                Toast.makeText(this, "Kata kunci tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun navigateToMusicPlayerWithMood(mood: String, emoji: String) {
        val moodMessage = when (mood) {
            "happy" -> "Lagi happy nih! Let's party! $emoji"
            "sad" -> "Gapapa sedih, keluarin aja. Kita temenin ya. $emoji"
            "energetic" -> "Gaspol! Ini musik biar makin semangat! $emoji"
            else -> "Oke, kita kasih yang santai biar rileks. $emoji"
        }
        Toast.makeText(this, moodMessage, Toast.LENGTH_LONG).show()

        val intent = Intent(this, MusicPlayerActivity::class.java).apply {
            putExtra("PLAYLIST_TYPE", "MOOD")
            putExtra("PLAYLIST_KEY", mood)
        }
        startActivity(intent)
    }

    private fun navigateToMusicPlayerWithSearch(query: String) {
        Toast.makeText(this, "Mencari lagu untuk: '$query'", Toast.LENGTH_LONG).show()

        val intent = Intent(this, MusicPlayerActivity::class.java).apply {
            putExtra("PLAYLIST_TYPE", "SEARCH")
            putExtra("PLAYLIST_KEY", query)
        }
        startActivity(intent)
    }

    private fun navigateToFavorites() {
        Toast.makeText(this, "Membuka daftar lagu favorit...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MusicPlayerActivity::class.java).apply {
            putExtra("PLAYLIST_TYPE", "FAVORITES")
        }
        startActivity(intent)
    }

    private fun containsKeyword(text: String, keywords: List<String>): Boolean {
        for (word in keywords) {
            if (text.contains(word)) return true
        }
        return false
    }
}
