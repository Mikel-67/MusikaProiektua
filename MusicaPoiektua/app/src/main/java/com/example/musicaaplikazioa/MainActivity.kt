package com.example.musicaaplikazioa

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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

// Spotify Auth SDK
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse

import com.example.musicaaplikazioa.adapter.PostAdaptadorea
import com.example.musicaaplikazioa.models.Post

import com.google.firebase.firestore.FirebaseFirestore

import com.example.musicaaplikazioa.pantailak.HomePantaila
import com.example.musicaaplikazioa.pantailak.ProfilaPantaila
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout


class MainActivity : AppCompatActivity() {
    private lateinit var spotifyAuthManager: SpotifyAuthManager
    private lateinit var spotifyPlaybackManager: SpotifyPlaybackManager
    private lateinit var homePantaila: HomePantaila
    private var accessToken: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spotifyAuthManager = SpotifyAuthManager(this)

        // Start authentication
        spotifyAuthManager.authenticate()

        homePantaila = HomePantaila()

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    mostrarHome()
                    true
                }
                R.id.nav_profile -> {
                    mostrarPerfil()
                    true
                }
                R.id.nav_add -> {
                    val intent = Intent(this, AddActivity::class.java)
                    intent.putExtra("ACCESS_TOKEN", accessToken)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun mostrarHome() {
        val contenedor = findViewById<ConstraintLayout>(R.id.contenedorPrincipal)
        contenedor.removeAllViews()


        val homeView = layoutInflater.inflate(R.layout.home_pantaila, contenedor, false)
        contenedor.addView(homeView)
        val rvPosts: RecyclerView = findViewById(R.id.rvPosts)
        val pbKargatzen: ProgressBar = findViewById(R.id.pbKargatzen)

        homePantaila.sortu(rvPosts, pbKargatzen, this, spotifyPlaybackManager)
    }

    private fun mostrarPerfil() {
        val contenedor = findViewById<ConstraintLayout>(R.id.contenedorPrincipal)
        contenedor.removeAllViews()

        val perfilView = layoutInflater.inflate(R.layout.profila_pantaila, contenedor, false)
        contenedor.addView(perfilView)

        val ivProfilePic: ImageView = findViewById(R.id.ivProfilePic)
        val tvUserName: TextView = findViewById(R.id.tvUserName)
        val tvEmail: TextView = findViewById(R.id.tvEmail)
        val rvUserPosts: RecyclerView = findViewById(R.id.rvUserPosts)

        val profilaPantaila = ProfilaPantaila()
        profilaPantaila.sortu(this, ivProfilePic, tvUserName, tvEmail, rvUserPosts, userId = "1")
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
                //Home pantaila
                val rvPosts: RecyclerView = findViewById(R.id.rvPosts)
                val pbKargatzen: ProgressBar = findViewById(R.id.pbKargatzen)
                homePantaila.sortu(rvPosts, pbKargatzen, this,spotifyPlaybackManager)
                //setupPlaybackControls(playbackManager)
            } else {
                showError("Failed to connect to Spotify app. Please ensure Spotify is installed.")
            }
        }
    }
}