package com.example.musicaaplikazioa.models

import com.google.firebase.Timestamp

data class UserFirebase(
    var userName: String = "",
    var profileUrl: String = "",
    var email: String = "",
    var password: String = "",
    var spotifyId: String = "",
    var createdAt: Timestamp? = null
)