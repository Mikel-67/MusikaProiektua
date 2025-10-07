object SpotifyConstants {
    const val CLIENT_ID = "fbe179bce885430fa71ea9609a521ff3"
    const val REDIRECT_URI = "miapp://callback"
    const val REQUEST_CODE = 1337

    // Spotify scopes - adjust based on your needs
    val SCOPES = arrayOf(
        "user-read-private",
        "user-read-email",
        "user-read-playback-state",
        "user-modify-playback-state",
        "user-read-currently-playing",
        "playlist-read-private",
        "playlist-read-collaborative",
        "playlist-modify-public",
        "playlist-modify-private"
    )
}