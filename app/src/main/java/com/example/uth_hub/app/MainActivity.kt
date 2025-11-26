package com.example.uth_hub.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.app.navigation.NavGraph
import com.example.uth_hub.core.design.theme.Uth_hubTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 🔥 BẮT BUỘC CHO ANDROID 13+ (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
        enableEdgeToEdge()
        setContent {
            Uth_hubTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    // ⭐ Lưu token FCM khi mở app
    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val uid = FirebaseAuth.getInstance().uid ?: return@addOnSuccessListener

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update("fcmToken", token)
        }
    }

    // Điều hướng chính
    NavGraph(
        navController = navController,
        modifier = Modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Uth_hubTheme {
        MainApp()
    }
}
