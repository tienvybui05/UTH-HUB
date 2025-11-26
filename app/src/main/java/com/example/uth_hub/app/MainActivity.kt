package com.example.uth_hub.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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

        // Android 13+ Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        enableEdgeToEdge()

        val startRoute = resolveDeepLink(intent)

        setContent {
            Uth_hubTheme {
                val navController = rememberNavController()

                // Lưu token FCM
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
