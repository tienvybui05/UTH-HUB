package com.example.uth_hub.feature.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.post.data.PostRepository
import com.example.uth_hub.feature.post.domain.model.PostModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavedPostsViewModel(
    private val repo: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostModel>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _posts.value = repo.getSavedPostsByMe(limit = 100)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun toggleLike(postId: String) {
        _posts.value = _posts.value.map { p ->
            if (p.id == postId) {
                val nowLiked = !p.likedByMe
                p.copy(
                    likedByMe = nowLiked,
                    likeCount = (p.likeCount + if (nowLiked) 1 else -1).coerceAtLeast(0)
                )
            } else p
        }
        viewModelScope.launch {
            try {
                repo.toggleLike(postId)
            } catch (e: Exception) {
                refresh()
            }
        }
    }

    fun toggleSave(postId: String) {
        // Optimistic: bỏ lưu sẽ loại khỏi danh sách
        _posts.value = _posts.value.map { p ->
            if (p.id == postId) {
                val nowSaved = !p.savedByMe
                p.copy(
                    savedByMe = nowSaved,
                    saveCount = (p.saveCount + if (nowSaved) 1 else -1).coerceAtLeast(0)
                )
            } else p
        }
        viewModelScope.launch {
            try {
                repo.toggleSave(postId)
                _posts.value = _posts.value.filter { it.savedByMe }
            } catch (e: Exception) {
                refresh()
            }
        }
    }
}
