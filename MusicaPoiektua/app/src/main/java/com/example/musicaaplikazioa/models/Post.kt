package com.example.musicaaplikazioa.models

import com.example.musicaaplikazioa.models.SpotifyData

data class Post(
    val spotifyData: SpotifyData = SpotifyData(),
    val userId: String = ""
)