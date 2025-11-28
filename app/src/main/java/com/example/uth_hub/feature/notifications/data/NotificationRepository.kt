package com.example.uth_hub.feature.notifications.data

import android.util.Log
import com.example.uth_hub.feature.notifications.model.NotificationModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class NotificationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getNotifications(uid: String) = callbackFlow {

        val listener = firestore.collection("notifications")
            .whereEqualTo("receiverId", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->

                Log.d("NOTI_TEST", "Snapshot fired")

                // ⭐ Log lỗi để debug
                if (err != null) {
                    Log.e("NOTI_TEST", "ERROR = ${err.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                // ⭐ Log số lượng document trả về
                Log.d("NOTI_TEST", "Documents = ${snap?.documents?.size}")

                // ⭐ Map dữ liệu
                val list = snap?.documents?.map { doc ->
                    NotificationModel(
                        id = doc.id,
                        type = doc.getString("type") ?: "",
                        senderId = doc.getString("senderId") ?: "",
                        receiverId = doc.getString("receiverId") ?: "",
                        postId = doc.getString("postId") ?: "",
                        message = doc.getString("message") ?: "",
                        timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L,
                        isRead = doc.getBoolean("isRead") ?: false
                    )
                } ?: emptyList()

                trySend(list)
            }



        awaitClose {
            listener.remove()
        }
    }
    suspend fun deleteNotification(id: String) {
        firestore.collection("notifications")
            .document(id)
            .delete()
    }
}
