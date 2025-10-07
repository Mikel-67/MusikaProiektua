package com.example.musicaaplikazioa

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Aquí van tus modelos de datos
import com.example.musicaaplikazioa.models.User
import com.example.musicaaplikazioa.models.PlaylistResponse
import com.example.musicaaplikazioa.models.Playlist
import com.example.musicaaplikazioa.models.Image

interface SpotifyWebAPI {
    @GET("me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): Response<User>

    @GET("me/playlists")
    suspend fun getUserPlaylists(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 20
    ): Response<PlaylistResponse>

    @GET("me/player/currently-playing")
    suspend fun getCurrentlyPlaying(
        @Header("Authorization") authorization: String
    )

    @GET("search")
    suspend fun searchTracks(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 20
    )
}