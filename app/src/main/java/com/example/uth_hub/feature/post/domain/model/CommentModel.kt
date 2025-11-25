package com.example.uth_hub.feature.post.domain.model

import com.google.firebase.Timestamp

data class CommentModel(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val text: String = "",
    val createdAt: Timestamp? = null,

    // support reply & like
    val parentCommentId: String? = null,      // null / "" = comment gốc
    val likeCount: Long = 0L,
    val replyCount: Long = 0L,

    //media (ảnh / video)
    val mediaUrls: List<String> = emptyList(),// list ảnh / video
    val mediaType: String? = null,            // "image" | "video" | null

    //client-only
    val likedByMe: Boolean = false
)
