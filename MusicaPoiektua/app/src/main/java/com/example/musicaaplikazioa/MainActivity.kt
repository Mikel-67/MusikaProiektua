package com.example.musicaaplikazioa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Button
import android.widget.TextView

// Spotify Auth SDK
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.example.musicaaplikazioa.SpotifyAuthManager
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var spotifyAuthManager: SpotifyAuthManager
    private var accessToken: String? = null
    private lateinit var oraingoAbestia: TextView;
    private lateinit var tvPlayerState: TextView

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spotifyAuthManager = SpotifyAuthManager(this)

        // Start authentication
        spotifyAuthManager.authenticate()
        oraingoAbestia = findViewById(R.id.currentTrackText);
        tvPlayerState = findViewById(R.id.tvPlayerState);

        fetchSongTitle();
    }

    private fun fetchSongTitle() {
        val songRef = db.collection("posts").document("3")
        songRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val text = document.get("text")?.toString() ?: "No text found"
                    tvPlayerState.text = text
                } else {
                    tvPlayerState.text = "Documento '3' no existe"
                }
            }
            .addOnFailureListener { exception ->
                tvPlayerState.text = "Error al obtener datos: ${exception.message}"
            }
    }


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
                // Connection successful - you can now control playback
                setupPlaybackControls(playbackManager)
            } else {
                showError("Failed to connect to Spotify app. Please ensure Spotify is installed.")
            }
        }
    }

    private fun setupPlaybackControls(playbackManager: SpotifyPlaybackManager) {
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
    }
}