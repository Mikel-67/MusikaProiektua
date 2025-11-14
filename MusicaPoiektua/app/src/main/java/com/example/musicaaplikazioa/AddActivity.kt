package com.example.musicaaplikazioa

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.Toast
import com.example.musicaaplikazioa.adapter.SpotifySongAdapter
import com.example.musicaaplikazioa.models.SpotifyData

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AddActivity : AppCompatActivity() {

    private var accessToken: String? = null

    private val songs = mutableListOf<SpotifyData>()
    private lateinit var adapter: SpotifySongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val etSearchSong: EditText = findViewById(R.id.etSearchSong)
        val rvSongs: RecyclerView = findViewById(R.id.rvSongs)

        rvSongs.layoutManager = LinearLayoutManager(this)

        adapter = SpotifySongAdapter(songs) { song ->
            // Aquí abrirás diálogo para dar rating + comentario
            Toast.makeText(this, "Seleccionaste: ${song.name}", Toast.LENGTH_SHORT).show()
        }

        rvSongs.adapter = adapter

        // Tomar el token que venga de MainActivity
        accessToken = intent.getStringExtra("ACCESS_TOKEN")

        if (!accessToken.isNullOrEmpty()) {
            obtenerCancionesRecientesSpotify(accessToken!!) { recentSongs ->
                runOnUiThread {
                    songs.clear()
                    songs.addAll(recentSongs)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun obtenerCancionesRecientesSpotify(token: String, callback: (List<SpotifyData>) -> Unit) {
        val url = "https://api.spotify.com/v1/recommendations?" +
                "seed_artists=4NHQUGzhtTLFvgF5SZesLK" +
                "&seed_genres=classical,country" +
                "&seed_tracks=0c6xIDDpzE81m2q797ordA" +
                "&limit=10"

        Log.d("token", token)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()
        Log.d("request", request.toString())


        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    callback(emptyList())
                    return
                }

                try {
                    val json = JSONObject(bodyString)
                    val tracks = json.getJSONArray("tracks")
                    val songs = mutableListOf<SpotifyData>()

                    for (i in 0 until tracks.length()) {
                        val trackObj = tracks.getJSONObject(i)

                        val name = trackObj.getString("name")
                        val artist = trackObj.getJSONArray("artists").getJSONObject(0).getString("name")
                        val album = trackObj.getJSONObject("album").getString("name")
                        val imageUrl = trackObj.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url")
                        val trackId = trackObj.getString("id")

                        songs.add(SpotifyData(name, artist, album, imageUrl, trackId))
                    }
                    Log.d("SpotifyData", "Songs: $songs")
                    callback(songs)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(emptyList())
                }
            }

        })
    }
}

