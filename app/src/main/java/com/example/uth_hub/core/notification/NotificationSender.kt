package com.example.uth_hub.core.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object NotificationSender {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    /**
     * Gửi thông báo khi có người like bài viết
     *
     * @param postId    id bài post (document id trong collection "posts")
     * @param receiverId uid của chủ bài viết (người sẽ nhận thông báo)
     */
    fun sendLikeNotification(postId: String, receiverId: String) {
        val senderId = auth.currentUser?.uid ?: return

        // Tự like bài mình thì khỏi gửi thông báo
        if (senderId == receiverId) return

        val notiRef = firestore.collection("notifications").document()

        val data = hashMapOf(
            "id" to notiRef.id,
            "type" to "like",
            "senderId" to senderId,
            "receiverId" to receiverId,
            "postId" to postId,
            "message" to "Ai đó đã thích bài viết của bạn",
            "timestamp" to FieldValue.serverTimestamp(),
            "isRead" to false
        )

        notiRef.set(data, SetOptions.merge())
    }

    // Sau này muốn thêm comment/follow thì viết thêm hàm ở đây:
    // fun sendCommentNotification(...) { ... }
}
