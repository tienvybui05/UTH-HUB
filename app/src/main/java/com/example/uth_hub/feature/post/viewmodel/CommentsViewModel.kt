package com.example.uth_hub.feature.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.post.data.PostRepository
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.example.uth_hub.feature.post.domain.model.PostModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val postId: String,
    private val repo: PostRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _post = MutableStateFlow<PostModel?>(null)
    val post = _post.asStateFlow()

    private val _comments = MutableStateFlow<List<CommentModel>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _commentText = MutableStateFlow("")
    val commentText = _commentText.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    private val _sending = MutableStateFlow(false)
    val sending = _sending.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadPost()
        observeComments()
    }

    private fun loadPost() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val uid = auth.currentUser?.uid
                var p = repo.getPostById(postId)
                if (p != null && uid != null) {
                    val liked = repo.isLiked(postId, uid)
                    val saved = repo.isSaved(postId, uid)
                    p = p.copy(likedByMe = liked, savedByMe = saved)
                }
                _post.value = p
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private fun observeComments() {
        repo.observeComments(postId)
            .onEach { _comments.value = it }
            .catch { e -> _error.value = e.message }
            .launchIn(viewModelScope)
    }

    fun onCommentTextChange(new: String) {
        _commentText.value = new
    }

    fun sendComment() {
        val text = _commentText.value.trim()
        if (text.isEmpty() || _sending.value) return

        viewModelScope.launch {
            _sending.value = true
            try {
                repo.addComment(postId, text)
                _commentText.value = ""
                _post.value = _post.value?.copy(
                    commentCount = (_post.value?.commentCount ?: 0L) + 1L
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _sending.value = false
            }
        }
    }

    fun toggleLike() {
        val current = _post.value ?: return
        val nowLiked = !current.likedByMe
        val updated = current.copy(
            likedByMe = nowLiked,
            likeCount = (current.likeCount + if (nowLiked) 1 else -1).coerceAtLeast(0)
        )
        _post.value = updated

        viewModelScope.launch {
            try {
                repo.toggleLike(postId)
            } catch (e: Exception) {
                _post.value = current // rollback
                _error.value = e.message
            }
        }
    }

    fun toggleSave() {
        val current = _post.value ?: return
        val nowSaved = !current.savedByMe
        val updated = current.copy(
            savedByMe = nowSaved,
            saveCount = (current.saveCount + if (nowSaved) 1 else -1).coerceAtLeast(0)
        )
        _post.value = updated

        viewModelScope.launch {
            try {
                repo.toggleSave(postId)
            } catch (e: Exception) {
                _post.value = current
                _error.value = e.message
            }
        }
    }
}
