package com.example.uth_hub.feature.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uth_hub.feature.post.data.PostRepository
import com.google.firebase.auth.FirebaseAuth

class FeedViewModelFactory(
    private val repo: PostRepository,
    private val auth: FirebaseAuth
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(repo, auth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
