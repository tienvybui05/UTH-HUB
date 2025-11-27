package com.example.uth_hub.core.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object NotificationSender {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    /**
     * Gửi thông báo khi có người like bài viết — kèm tên thật
     */
    suspend fun sendLikeNotification(postId: String, receiverId: String) {
        val senderId = auth.currentUser?.uid ?: return

        // Không gửi nếu tự like bài mình
        if (senderId == receiverId) return

        // ===== 🔥 LẤY TÊN VÀ AVATAR NGƯỜI LIKE =====
        val userDoc = firestore.collection("users").document(senderId).get().await()

        val senderName =
            userDoc.getString("displayName")
                ?: userDoc.getString("name")
                ?: auth.currentUser?.displayName
                ?: "Người dùng"

        val senderAvatar =
            userDoc.getString("photoUrl")
                ?: userDoc.getString("avatarUrl")
                ?: auth.currentUser?.photoUrl?.toString()
                ?: ""

        // ===== 🔥 TẠO DOCUMENT THÔNG BÁO =====
        val notiRef = firestore.collection("notifications").document()

        val data = hashMapOf(
            "id" to notiRef.id,
            "type" to "like",
            "senderId" to senderId,
            "senderName" to senderName,       // 👈 thêm tên người like nè
            "senderAvatar" to senderAvatar,   // 👈 thêm avatar nếu muốn show UI đẹp
            "receiverId" to receiverId,
            "postId" to postId,
            "message" to "$senderName đã thích bài viết của bạn",
            "timestamp" to FieldValue.serverTimestamp(),
            "isRead" to false
        )

        notiRef.set(data, SetOptions.merge())
    }

    /**
     * Gửi thông báo khi có người comment bài viết
     */
    suspend fun sendCommentNotification(
        postId: String,
        receiverId: String,
        commentContent: String
    ) {
        val senderId = auth.currentUser?.uid ?: return

        // Không gửi nếu tự comment bài mình
        if (senderId == receiverId) return

        // ===== 🔥 LẤY TÊN VÀ AVATAR NGƯỜI COMMENT =====
        val userDoc = firestore.collection("users").document(senderId).get().await()

        val senderName =
            userDoc.getString("displayName")
                ?: userDoc.getString("name")
                ?: auth.currentUser?.displayName
                ?: "Người dùng"

        val senderAvatar =
            userDoc.getString("photoUrl")
                ?: userDoc.getString("avatarUrl")
                ?: auth.currentUser?.photoUrl?.toString()
                ?: ""

        // ===== 🔥 TẠO DOCUMENT THÔNG BÁO =====
        val notiRef = firestore.collection("notifications").document()

        val data = hashMapOf(
            "id" to notiRef.id,
            "type" to "comment",
            "senderId" to senderId,
            "senderName" to senderName,
            "senderAvatar" to senderAvatar,
            "receiverId" to receiverId,
            "postId" to postId,
            "commentContent" to commentContent,
            "message" to "$senderName đã bình luận: $commentContent",
            "timestamp" to FieldValue.serverTimestamp(),
            "isRead" to false
        )

        notiRef.set(data, SetOptions.merge())
    }

}
