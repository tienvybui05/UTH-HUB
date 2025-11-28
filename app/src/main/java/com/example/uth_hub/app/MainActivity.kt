package com.example.uth_hub.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.app.navigation.NavGraph
import com.example.uth_hub.core.design.theme.Uth_hubTheme
import com.example.uth_hub.feature.deeplink.DeepLinkResolver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        enableEdgeToEdge()

        val startRoute = resolveDeepLink(intent)

        setContent {

            // 🔥 KHÓA FONT SCALE để UI không bị nở chữ
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = 1f        // ép chữ = 1.0 → không bị phóng to
                )
            ) {

                Uth_hubTheme {

                    val navController = rememberNavController()

                    // Lưu FCM token
                    LaunchedEffect(Unit) {
                        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                            val uid = FirebaseAuth.getInstance().uid ?: return@addOnSuccessListener

                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .update("fcmToken", token)
                        }
                    }

                    NavGraph(
                        navController = navController,
                        modifier = Modifier,
                        startDeepLink = startRoute
                    )
                }
            }
        }
    }

    private fun resolveDeepLink(intent: Intent?): String? {
        val uri: Uri? = intent?.data ?: return null
        val dest = DeepLinkResolver.resolve(uri)

        return when (dest) {
            is DeepLinkResolver.Destination.OtherProfile ->
                "otherProfile/${dest.userKey}"
            else -> null
        }
    }
}
