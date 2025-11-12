package com.example.uth_hub.feature.post.viewmodel

data class CreatePostUiState(
    val posting: Boolean = false,
    val error: String? = null,
    val postedId: String? = null
)