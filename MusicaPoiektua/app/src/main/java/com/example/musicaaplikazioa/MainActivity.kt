package com.example.musicaaplikazioa

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

// Spotify Auth SDK
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.example.musicaaplikazioa.SpotifyAuthManager
import com.example.musicaaplikazioa.SpotifyPlaybackManager
import com.example.musicaaplikazioa.adapter.PostAdaptadorea
import com.example.musicaaplikazioa.models.Post


import com.example.musicaaplikazioa.models.SpotifyData

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import okhttp3.internal.http2.Http2Reader


class MainActivity : AppCompatActivity() {
    private lateinit var spotifyAuthManager: SpotifyAuthManager
    private lateinit var spotifyPlaybackManager: SpotifyPlaybackManager
    private var accessToken: String? = null
    private lateinit var oraingoAbestia: TextView;
    private lateinit var db: FirebaseFirestore
    lateinit var rvPost: RecyclerView
    lateinit var pbKargatzen: ProgressBar
    private lateinit var adapter: PostAdaptadorea

    var postLista: ArrayList<Post> = ArrayList()
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spotifyAuthManager = SpotifyAuthManager(this)

        rvPost = findViewById(R.id.rvPosts)
        rvPost.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        PagerSnapHelper().attachToRecyclerView(rvPost)
        pbKargatzen = findViewById(R.id.pbKargatzen)

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
                    // Llegaste al final
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
        // Start authentication
        spotifyAuthManager.authenticate()
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


    //Spotify funtzioak
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SpotifyConstants.REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    accessToken = response.accessToken
                    onAuthenticationComplete(response.accessToken)
                }

                AuthorizationResponse.Type.ERROR -> {
                    Log.e("SpotifyAuth", "Auth error: ${response.error}")
                    showError("Authentication failed: ${response.error}")
                }

                else -> {
                    Log.d("SpotifyAuth", "Auth cancelled")
                }
            }
        }
    }

    private fun onAuthenticationComplete(token: String?) {
        // Token received successfully
        Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()

        // Now you can use the Web API
        setupSpotifyWebAPI(token)

        // And connect to the App Remote
        connectToSpotifyAppRemote()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setupSpotifyWebAPI(accessToken: String?) {
        val api = SpotifyApiClient.create()

        lifecycleScope.launch {
            try {
                val authHeader = "Bearer $accessToken"

                // Get current user
                val userResponse = api.getCurrentUser(authHeader)
                if (userResponse.isSuccessful) {
                    val user = userResponse.body()
                    Log.d("SpotifyAPI", "Welcome ${user?.display_name}")
                }

                // Get user's playlists
                val playlistsResponse = api.getUserPlaylists(authHeader)
                if (playlistsResponse.isSuccessful) {
                    val playlists = playlistsResponse.body()?.items
                    Log.d("SpotifyAPI", "Found ${playlists?.size} playlists")
                }

            } catch (e: Exception) {
                Log.e("SpotifyAPI", "API Error: ${e.message}")
            }
        }
    }
    private fun connectToSpotifyAppRemote() {
        val playbackManager = SpotifyPlaybackManager(this)

        playbackManager.connect { success ->
            if (success) {
                spotifyPlaybackManager = playbackManager
                //setupPlaybackControls(playbackManager)
            } else {
                showError("Failed to connect to Spotify app. Please ensure Spotify is installed.")
            }
        }
    }

    /*private fun setupPlaybackControls(playbackManager: SpotifyPlaybackManager) {
        findViewById<Button>(R.id.btn_play).setOnClickListener {
            // Play a specific track or playlist
            playbackManager.play("spotify:track:6D87OewwdEhhSUWij4jUKp")
            playbackManager.getCurrentSongInfo(oraingoAbestia)
        }

        findViewById<Button>(R.id.btn_pause).setOnClickListener {
            playbackManager.pause()
        }

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            playbackManager.skipToNext()
        }

        // Get current player state
        playbackManager.getCurrentPlayerState { playerState ->
            playerState?.let { state ->
                val isPlaying = !state.isPaused
                val currentTrack = state.track
                Log.d("PlayerState", "Playing: $isPlaying, Track: ${currentTrack.name}")
            }
        }
    }*/

}