package com.example.musicaaplikazioa

import android.app.Activity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyAuthManager(private val activity: Activity) {

    fun authenticate() {
        val builder = AuthorizationRequest.Builder(
            SpotifyConstants.CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            SpotifyConstants.REDIRECT_URI
        )

        builder.setScopes(SpotifyConstants.SCOPES)
        builder.setShowDialog(false)

        val request = builder.build()
        AuthorizationClient.openLoginActivity(
            activity,
            SpotifyConstants.REQUEST_CODE,
            request
        )
    }
}
