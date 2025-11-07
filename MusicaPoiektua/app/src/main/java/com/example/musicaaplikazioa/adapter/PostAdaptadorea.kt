package com.example.musicaaplikazioa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicaaplikazioa.R
import com.example.musicaaplikazioa.models.Post

class PostAdaptadorea(private val postList: List<Post>) :
    RecyclerView.Adapter<PostAdaptadorea.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]

        // Cargar imagen del álbum
        Glide.with(holder.itemView.context)
            .load(post.spotifyData.url)
            .placeholder(R.drawable.ic_music_placeholder)
            .into(holder.ivAlbumCover)

        // Asignar textos
        holder.tvTrackName.text = post.spotifyData.name
        holder.tvArtist.text = post.spotifyData.artist
        holder.tvLikesCount.text = "${post.likesCount} Likes"
        holder.tvRate.text = "★ ${post.rate}"
    }

    override fun getItemCount(): Int = postList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAlbumCover: ImageView = itemView.findViewById(R.id.ivAlbumCover)
        val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val tvLikesCount: TextView = itemView.findViewById(R.id.tvLikesCount)
        val tvRate: TextView = itemView.findViewById(R.id.tvRate)
    }
}
