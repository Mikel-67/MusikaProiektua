package com.example.musicaaplikazioa

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState


class SpotifyPlaybackManager(private val context: Context) {
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val connectionParams = ConnectionParams.Builder(SpotifyConstants.CLIENT_ID)
        .setRedirectUri(SpotifyConstants.REDIRECT_URI)
        .showAuthView(true)
        .build()

    fun connect(callback: (Boolean) -> Unit) {
        SpotifyAppRemote.connect(
            context,
            connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    Log.d("SpotifyRemote", "Connected successfully")
                    callback(true)
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("SpotifyRemote", "Connection failed", throwable)
                    callback(false)
                }
            }
        )
    }

    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    fun play(uri: String) {
        spotifyAppRemote?.playerApi?.play(uri)
    }

    fun pause() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resume() {
        spotifyAppRemote?.playerApi?.resume()
    }

    fun skipToNext() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    fun skipToPrevious() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }

    fun getCurrentPlayerState(callback: (PlayerState?) -> Unit) {
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { playerState ->
            callback(playerState)
        }
    }
    fun getCurrentSongInfo(oraingoAbestia: TextView){
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback {
            playerState -> val track = playerState.track
            if (track != null) {
                val songInfo = "Título: ${track.name}\nArtista: ${track.artist.name}\nÁlbum: ${track.album.name} \n ${track.uri}"
                oraingoAbestia.text = songInfo
            }
        }
    }
}