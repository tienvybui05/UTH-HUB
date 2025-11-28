package com.example.uth_hub.feature.deeplink

import android.net.Uri
object DeepLinkResolver {

    sealed class Destination {
        data class OtherProfile(val userKey: String) : Destination()
        object None : Destination()
    }

    fun resolve(uri: Uri?): Destination {
        if (uri == null) return Destination.None

        val scheme = uri.scheme ?: ""
        val host = uri.host ?: ""
        val segments = uri.pathSegments

        // ==============================
        // FIREBASE HOSTING APP LINK
        // https://<your-domain>/user/{uid}
        // ==============================
        if (scheme == "https" &&
            host.equals(AppLinkConfig.BASE_HOST, ignoreCase = true) &&
            segments.size >= 2 &&
            segments[0] == "user"
        ) {
            val uid = segments[1]
            if (uid.isNotBlank()) {
                return Destination.OtherProfile(uid)
            }
        }

        // Không khớp mẫu nào
        return Destination.None
    }
}
