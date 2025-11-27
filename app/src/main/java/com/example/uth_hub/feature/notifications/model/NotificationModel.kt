package com.example.uth_hub.feature.notifications.model

data class NotificationModel(
    val id: String = "",
    val type: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val postId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false
)
