package com.example.musicaaplikazioa.models

import com.google.firebase.Timestamp

data class Post(
    val createdAt: Timestamp? = null,
    val likesCount: Int = 0,
    val rate: Float = 0f,
    val spotifyData: SpotifyData = SpotifyData(),
    val text: String = "",
    val userId: String = ""
)