package com.example.musicaaplikazioa.pantailak

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.musicaaplikazioa.SpotifyPlaybackManager
import com.example.musicaaplikazioa.adapter.PostAdaptadorea
import com.example.musicaaplikazioa.models.Post
import com.google.firebase.firestore.FirebaseFirestore

class HomePantaila {
    lateinit var pbKargatzen: ProgressBar
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: PostAdaptadorea
    var postLista: ArrayList<Post> = ArrayList()
    private var isLoading = false

    public fun sortu (rvPost: RecyclerView, pbKargatzu: ProgressBar, thisContext: Context, spotifyPlaybackManager: SpotifyPlaybackManager) {
        pbKargatzen = pbKargatzu
        rvPost.layoutManager = LinearLayoutManager(thisContext, RecyclerView.VERTICAL, false)
        PagerSnapHelper().attachToRecyclerView(rvPost)

        db = FirebaseFirestore.getInstance()
        adapter = PostAdaptadorea(postLista)

        rvPost.adapter = adapter

        abestiakEskuratu()

        rvPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()

                    if (position != RecyclerView.NO_POSITION && position < postLista.size) {
                        val currentPost = postLista[position]
                        val trackId = currentPost.spotifyData.spotifyTrackId

                        if (!trackId.isNullOrEmpty()) {
                            spotifyPlaybackManager.getCurrentSongInfo()
                            val uri = "spotify:track:$trackId"
                            spotifyPlaybackManager.play(uri)
                            Log.d("Spotify", "🎵 Reproduciendo canción con ID: $uri")
                        } else {
                            Log.w("Spotify", "⚠️ Este post no tiene trackId.")
                        }
                    }
                }

                val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()

                if (!isLoading && lastVisible == adapter.itemCount - 1) {
                    isLoading = true
                    pbKargatzen.visibility = View.VISIBLE

                    Handler().postDelayed({
                        abestiakEskuratu()
                        isLoading = false
                        pbKargatzen.visibility = View.GONE
                    }, 1000)
                }
            }
        })
    }
    private fun abestiakEskuratu() {
        db.collection("posts")
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    postLista.add(post)
                }
                adapter.notifyDataSetChanged()
                pbKargatzen.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener posts", e)
                pbKargatzen.visibility = View.GONE
            }
    }
}