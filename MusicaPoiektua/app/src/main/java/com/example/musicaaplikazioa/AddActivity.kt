package com.example.musicaaplikazioa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.musicaaplikazioa.adapter.SpotifySongAdapter
import com.example.musicaaplikazioa.models.SpotifyData
import com.google.firebase.firestore.FirebaseFirestore

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AddActivity : AppCompatActivity() {

    private var accessToken: String? = null
    private var userId: String? = null

    private val songs = mutableListOf<SpotifyData>()
    private lateinit var adapter: SpotifySongAdapter
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        userId = intent.getStringExtra("USER_ID")

        db = FirebaseFirestore.getInstance()

        val etSearchSong: EditText = findViewById(R.id.etSearchSong)
        val rvSongs: RecyclerView = findViewById(R.id.rvSongs)
        rvSongs.layoutManager = LinearLayoutManager(this)

        // Lista de canciones default
        val defaultSongs = listOf(
            SpotifyData(
                name = "Soy lo Peor",
                artist = "Depresión Sonora, Bb trickz",
                album = "DepresionTrickz",
                url = "https://i.scdn.co/image/ab67616d0000b273b669a0ab6ece8427f71870e5",
                spotifyTrackId = "3gddoedYU7FMPAZnLUbzkO"
            ),
            SpotifyData(
                name = "oppos",
                artist = "Sticky M.A.",
                album = "el curioso caso de benjamin manto ",
                url = "https://i.scdn.co/image/ab67616d0000b2730e807379945953279c94f453",
                spotifyTrackId = "6JGpSV2WYrCdcbGT4uYY3P"
            ),
            SpotifyData(
                name = "Como enamorarme",
                artist = "mvrk",
                album = "Como enamorarme",
                url = "https://i.scdn.co/image/ab67616d0000b27324776c050b333ecc827d0ef5",
                spotifyTrackId = "16RcB6ae7pNeCQVIrPuytu"
            ),
            SpotifyData(
                name = "superestrella",
                artist = "tarchi",
                album = "1888948",
                url = "https://i.scdn.co/image/ab67616d0000b273d1418f0bb6b3962350a3706b",
                spotifyTrackId = "7ezQMFunEJszWQVHJHju45"
            ),
            SpotifyData(
                name = "Gigante de Cristal",
                artist = "Barry B",
                album = "INFANCIA MAL CALIBRADA ",
                url = "https://i.scdn.co/image/ab67616d0000b273b34ccc816699d10be9689f22",
                spotifyTrackId = "6mIw2Ui7VBJMI4ybvWubl6"
            )
        )

        adapter = SpotifySongAdapter(defaultSongs) { song ->
            mostrarDialogoPost(song, userId!!)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
            finish()
        }

        rvSongs.adapter = adapter
    }
    fun igoPost(userId: String, song: SpotifyData, rating: Int, comentario: String) {
        val post = hashMapOf(
            "createdAt" to com.google.firebase.Timestamp.now(),
            "likesCount" to 0,
            "rate" to rating,
            "spotifyData" to hashMapOf(
                "album" to song.album,
                "artist" to song.artist,
                "name" to song.name,
                "spotifyTrackId" to song.spotifyTrackId,
                "url" to song.url
            ),
            "text" to comentario,
            "userId" to userId
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener { docRef ->
                Log.d("Firestore", "Post subido con ID: ${docRef.id}")
                Toast.makeText(this, "Post subido!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error subiendo post", e)
                Toast.makeText(this, "Error al subir post", Toast.LENGTH_SHORT).show()
            }
    }
    private fun mostrarDialogoPost(song: SpotifyData, userId: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_post, null)
        val etComentario = dialogView.findViewById<EditText>(R.id.etComentario)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        AlertDialog.Builder(this)
            .setTitle("Agregar post")
            .setView(dialogView)
            .setPositiveButton("Subir") { _, _ ->
                val comentario = etComentario.text.toString()
                val rating = ratingBar.rating.toInt()
                igoPost(userId, song, rating, comentario)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

