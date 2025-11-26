package com.example.uth_hub.feature.deeplink

object AppLinkConfig {
    const val BASE_SCHEME = "https"
    const val BASE_HOST = "uth-hub-49b77.web.app"
    private const val PROFILE_PATH_PREFIX = "/user"

    fun buildProfileUrl(uid: String): String {
        return "$BASE_SCHEME://$BASE_HOST$PROFILE_PATH_PREFIX/$uid"
    }
}

