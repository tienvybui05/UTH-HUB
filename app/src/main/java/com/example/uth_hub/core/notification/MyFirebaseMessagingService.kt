package com.example.uth_hub.core.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.uth_hub.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("UTHHub_FCM", "New token: $token")
        // TODO: (Tuỳ chọn) gửi token mới lên Firestore nếu muốn
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "UTH-Hub"
        val body = message.notification?.body ?: "Bạn có thông báo mới!"

        showNotification(title, body)
    }

    @SuppressLint("MissingPermission") // ⚡ Chỉ suppress nếu đã check permission
    private fun showNotification(title: String, body: String) {
        val channelId = "uth_hub_channel"

        // ===== 🔥 Tạo Notification Channel cho Android 8+ =====
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "UTH-Hub Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)   // phải có icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // ===== 🔥 FIX WARNING – CHECK QUYỀN TRƯỚC KHI notify =====
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                Log.w("UTHHub_FCM", "Chưa có quyền POST_NOTIFICATIONS, không thể notify()")
                return
            }
        }

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}
