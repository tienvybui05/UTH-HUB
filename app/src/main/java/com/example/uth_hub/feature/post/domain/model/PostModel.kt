package com.example.uth_hub.feature.post.domain.model

import com.google.firebase.Timestamp

data class PostModel(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorHandle: String = "",
    val authorInstitute: String = "",
    val authorAvatarUrl: String = "",
    val content: String = "",
    val imageUrls: List<String> = emptyList(),
    val createdAt: Timestamp? = null,
    val likeCount: Long = 0,
    val commentCount: Long = 0,
    val saveCount: Long = 0,

    // client-side convenience flags (không lưu Firestore)
    val likedByMe: Boolean = false,
    val savedByMe: Boolean = false
)
