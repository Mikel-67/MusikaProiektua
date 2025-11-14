package com.example.musicaaplikazioa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicaaplikazioa.R
import com.example.musicaaplikazioa.models.SpotifyData

class SpotifySongAdapter(
    private val songList: List<SpotifyData>,
    private val onItemClick: (SpotifyData) -> Unit
) : RecyclerView.Adapter<SpotifySongAdapter.SongViewHolder>() {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val tvSongName: TextView = itemView.findViewById(R.id.tvSongName)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spotify_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.tvSongName.text = song.name
        holder.tvArtist.text = song.artist
        Glide.with(holder.itemView.context)
            .load(song.url)
            .centerCrop()
            .into(holder.ivCover)

        holder.itemView.setOnClickListener { onItemClick(song) }
    }

    override fun getItemCount(): Int = songList.size
}
