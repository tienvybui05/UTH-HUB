package com.example.uth_hub.feature.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uth_hub.feature.post.data.PostRepository

class CreatePostViewModelFactory(
    private val repo: PostRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatePostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreatePostViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
