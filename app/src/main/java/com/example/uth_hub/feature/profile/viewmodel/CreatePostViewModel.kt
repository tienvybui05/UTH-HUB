package com.example.uth_hub.feature.post.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.post.data.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreatePostUiState(
    val posting: Boolean = false,
    val error: String? = null,
    val postedId: String? = null
)

class CreatePostViewModel(private val repo: PostRepository) : ViewModel() {
    private val _ui = MutableStateFlow(CreatePostUiState())
    val ui = _ui.asStateFlow()

    fun create(
        content: String,
        images: List<Uri>,
        authorName: String,
        authorHandle: String,
        authorInstitute: String,
        authorAvatarUrl: String
    ) {
        viewModelScope.launch {
            _ui.value = CreatePostUiState(posting = true)
            try {
                val id = repo.createPost(
                    content, images, authorName, authorHandle, authorInstitute, authorAvatarUrl
                )
                _ui.value = CreatePostUiState(posting = false, postedId = id)
            } catch (e: Exception) {
                _ui.value = CreatePostUiState(posting = false, error = e.message)
            }
        }
    }
}
