package com.example.musicaaplikazioa.models

data class User(
    val id: String,
    val display_name: String?,
    val email: String?,
    val images: List<Image>?
)

data class PlaylistResponse(
    val items: List<Playlist>,
    val total: Int
)

data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val uri: String,
    val images: List<Image>?
)

data class Image(
    val url: String,
    val height: Int?,
    val width: Int?
)