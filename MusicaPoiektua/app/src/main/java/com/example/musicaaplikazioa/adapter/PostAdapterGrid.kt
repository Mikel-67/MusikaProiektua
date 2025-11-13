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

class PostAdapterGrid(private val postList: List<Post>) :
    RecyclerView.Adapter<PostAdapterGrid.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
        val tvSongName: TextView = itemView.findViewById(R.id.tvSongName)
        val tvSongRating: TextView = itemView.findViewById(R.id.tvSongRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_grid, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]

        holder.tvSongName.text = post.spotifyData.name
        holder.tvSongRating.text = "${post.rate} ⭐"

        Glide.with(holder.itemView.context)
            .load(post.spotifyData.url)
            .centerCrop()
            .into(holder.ivPostImage)
    }

    override fun getItemCount(): Int = postList.size
}
