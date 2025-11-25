package com.example.uth_hub.feature.post.domain.model

import com.google.firebase.Timestamp

data class CommentModel(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val text: String = "",
    val createdAt: Timestamp? = null
)
