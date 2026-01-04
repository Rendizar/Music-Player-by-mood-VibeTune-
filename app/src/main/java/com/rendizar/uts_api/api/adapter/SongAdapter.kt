package com.rendizar.uts_api.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rendizar.uts_api.R
import com.rendizar.uts_api.api.model.Song

class SongAdapter(private val onSongClicked: (Song) -> Unit) :
    ListAdapter<Song, SongAdapter.SongViewHolder>(SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
        holder.itemView.setOnClickListener { onSongClicked(song) }
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSongTitle: TextView = itemView.findViewById(R.id.tvSongTitle)
        private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
        private val ivAlbumArt: ImageView = itemView.findViewById(R.id.ivAlbumArt)

        fun bind(song: Song) {
            tvSongTitle.text = song.title ?: "Judul Tidak Diketahui"
            tvArtistName.text = song.artist ?: "Artis Tidak Diketahui"

            val imageUrl = song.albumArtUrl
            
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background) 
                .error(R.drawable.ic_launcher_background) 
                .into(ivAlbumArt)
        }
    }

    class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
}
