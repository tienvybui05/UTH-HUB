package com.example.uth_hub.feature.post.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.post.data.PostRepository
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.example.uth_hub.feature.post.domain.model.PostModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    //đang reply comment nào
    private val _replyingTo = MutableStateFlow<CommentModel?>(null)
    val replyingTo = _replyingTo.asStateFlow()

    //media cho comment (ảnh / video)
    private val _commentMediaUris = MutableStateFlow<List<Uri>>(emptyList())
    val commentMediaUris = _commentMediaUris.asStateFlow()

    private val _commentMediaType = MutableStateFlow<String?>(null)
    val commentMediaType = _commentMediaType.asStateFlow()

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
            .onEach { newList ->
                // giữ lại trạng thái likedByMe cũ khi snapshot mới về
                val old = _comments.value
                val merged = newList.map { incoming ->
                    val oldMatch = old.firstOrNull { it.id == incoming.id }
                    if (oldMatch != null) {
                        incoming.copy(likedByMe = oldMatch.likedByMe)
                    } else {
                        incoming
                    }
                }
                _comments.value = merged
            }
            .catch { e -> _error.value = e.message }
            .launchIn(viewModelScope)
    }

    fun onCommentTextChange(new: String) {
        _commentText.value = new
    }

    // ===== Reply & Media state =====

    fun setReplyingTo(comment: CommentModel?) {
        _replyingTo.value = comment
    }

    fun setCommentMedia(uris: List<Uri>, type: String?) {
        _commentMediaUris.value = uris
        _commentMediaType.value = type
    }

    fun clearCommentMedia() {
        _commentMediaUris.value = emptyList()
        _commentMediaType.value = null
    }

    // ===== Gửi comment / reply =====

    fun sendComment() {
        val text = _commentText.value.trim()
        if (text.isEmpty() || _sending.value) return

        val parentId = _replyingTo.value?.id
        val mediaUris = _commentMediaUris.value
        val mediaType = _commentMediaType.value

        viewModelScope.launch {
            _sending.value = true
            try {
                repo.addComment(
                    postId = postId,
                    text = text,
                    parentCommentId = parentId,
                    mediaUris = mediaUris,
                    mediaType = mediaType
                )

                _commentText.value = ""
                clearCommentMedia()

                // tăng tổng comment của post
                _post.value = _post.value?.copy(
                    commentCount = (_post.value?.commentCount ?: 0L) + 1L
                )

                // nếu là reply thì tăng replyCount local cho parent
                if (parentId != null) {
                    val list = _comments.value.toMutableList()
                    val idx = list.indexOfFirst { it.id == parentId }
                    if (idx != -1) {
                        val parent = list[idx]
                        list[idx] = parent.copy(replyCount = parent.replyCount + 1)
                        _comments.value = list
                    }
                    _replyingTo.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _sending.value = false
            }
        }
    }

    // ===== Like post =====

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

    // ===== Like comment =====

    fun toggleCommentLike(target: CommentModel) {
        viewModelScope.launch {
            val currentList = _comments.value
            val idx = currentList.indexOfFirst { it.id == target.id }
            if (idx == -1) return@launch

            val old = currentList[idx]
            val nowLiked = !old.likedByMe
            val updated = old.copy(
                likedByMe = nowLiked,
                likeCount = (old.likeCount + if (nowLiked) 1 else -1).coerceAtLeast(0)
            )

            _comments.value = currentList.toMutableList().also { it[idx] = updated }

            try {
                repo.toggleCommentLike(postId, target.id)
            } catch (e: Exception) {
                _comments.value = currentList // rollback
                _error.value = e.message
            }
        }
    }
}
