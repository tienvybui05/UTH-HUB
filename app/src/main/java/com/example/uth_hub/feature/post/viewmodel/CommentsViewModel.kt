package com.example.uth_hub.feature.post.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.post.data.PostRepository
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.example.uth_hub.feature.post.domain.model.PostModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val post: StateFlow<PostModel?> = _post.asStateFlow()

    private val _comments = MutableStateFlow<List<CommentModel>>(emptyList())
    val comments: StateFlow<List<CommentModel>> = _comments.asStateFlow()

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _sending = MutableStateFlow(false)
    val sending: StateFlow<Boolean> = _sending.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ==== REPLY TO COMMENT ====
    private val _replyingTo = MutableStateFlow<CommentModel?>(null)
    val replyingTo: StateFlow<CommentModel?> = _replyingTo.asStateFlow()

    // ==== EDIT COMMENT ====
    private val _editingComment = MutableStateFlow<CommentModel?>(null)
    val editingComment: StateFlow<CommentModel?> = _editingComment.asStateFlow()

    // ==== MEDIA CHO COMMENT ====
    private val _commentMediaUris = MutableStateFlow<List<Uri>>(emptyList())
    val commentMediaUris: StateFlow<List<Uri>> = _commentMediaUris.asStateFlow()

    private val _commentMediaType = MutableStateFlow<String?>(null)  // "image" hoặc "video"
    val commentMediaType: StateFlow<String?> = _commentMediaType.asStateFlow()

    // current user id (dùng để check quyền)
    val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        loadPost()
        observeComments()
    }

    // ==========================
    // LOAD POST + TRẠNG THÁI LIKE/SAVE CỦA MÌNH
    // ==========================
    private fun loadPost() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val p = repo.getPostById(postId)
                val uid = auth.currentUser?.uid

                _post.value = if (p != null && uid != null) {
                    // check Firestore xem mình đã like / save post này chưa
                    val liked = repo.isLiked(postId, uid)
                    val saved = repo.isSaved(postId, uid)
                    p.copy(
                        likedByMe = liked,
                        savedByMe = saved
                    )
                } else {
                    p
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private fun observeComments() {
        repo.observeComments(postId)
            .onEach { list ->
                _comments.value = list

                // 🔄 đồng bộ lại commentCount của Post theo số lượng comment hiện tại
                val currentPost = _post.value
                if (currentPost != null) {
                    _post.value = currentPost.copy(
                        commentCount = list.size.toLong()
                    )
                }

                // đồng bộ lại replying / editing nếu list thay đổi
                val replyId = _replyingTo.value?.id
                val editId = _editingComment.value?.id

                _replyingTo.value = replyId?.let { id -> list.find { it.id == id } }
                _editingComment.value = editId?.let { id -> list.find { it.id == id } }
            }
            .catch { e ->
                _error.value = e.message
            }
            .launchIn(viewModelScope)
    }

    fun onCommentTextChange(text: String) {
        _commentText.value = text
    }

    // ============ MEDIA ============

    fun setMedia(uris: List<Uri>, type: String) {
        //  Chỉ giữ đúng 1 media
        _commentMediaUris.value = uris.take(1)
        _commentMediaType.value = type   // "image" hoặc "video"
    }

    fun clearMedia() {
        _commentMediaUris.value = emptyList()
        _commentMediaType.value = null
    }

    // ============ REPLY ============

    fun startReplyTo(comment: CommentModel) {
        _replyingTo.value = comment
        // nếu đang chỉnh sửa thì bỏ trạng thái chỉnh sửa
        _editingComment.value = null
    }

    fun clearReply() {
        _replyingTo.value = null
    }

    // ============ EDIT COMMENT ============

    fun startEditComment(comment: CommentModel) {
        val uid = auth.currentUser?.uid
        if (uid == null || comment.authorId != uid) return

        _editingComment.value = comment
        _commentText.value = comment.text

        // sửa text => tạm thời không sửa media
        clearReply()
        clearMedia()
    }

    fun cancelEditComment() {
        _editingComment.value = null
    }

    fun deleteComment(comment: CommentModel) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            if (uid == null || comment.authorId != uid) {
                _error.value = "Bạn không thể xóa bình luận của người khác"
                return@launch
            }

            try {
                repo.deleteComment(postId, comment)

                if (_editingComment.value?.id == comment.id) {
                    _editingComment.value = null
                    _commentText.value = ""
                }
                if (_replyingTo.value?.id == comment.id) {
                    _replyingTo.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // ============ LIKE / SAVE POST ============

    fun toggleLike(postId: String, postAuth: String) {
        val current = _post.value ?: return
        val currentlyLiked = current.likedByMe
        val newLikeCount =
            (current.likeCount + if (currentlyLiked) -1 else 1).coerceAtLeast(0)

        //  Optimistic update giống HomeScreen
        _post.value = current.copy(
            likedByMe = !currentlyLiked,
            likeCount = newLikeCount
        )

        viewModelScope.launch {
            try {
                repo.toggleLike(postId, postAuth)
            } catch (e: Exception) {
                // lỗi -> rollback lại state cũ
                _error.value = e.message
                _post.value = current
            }
        }
    }

    fun toggleSave() {
        val current = _post.value ?: return
        val currentlySaved = current.savedByMe
        val newSaveCount =
            (current.saveCount + if (currentlySaved) -1 else 1).coerceAtLeast(0)

        //  Optimistic update
        _post.value = current.copy(
            savedByMe = !currentlySaved,
            saveCount = newSaveCount
        )

        viewModelScope.launch {
            try {
                repo.toggleSave(postId)
            } catch (e: Exception) {
                _error.value = e.message
                _post.value = current
            }
        }
    }

    // ============ LIKE COMMENT ============

    fun toggleCommentLike(comment: CommentModel) {
        viewModelScope.launch {
            // Optimistic UI
            val currentList = _comments.value
            _comments.value = currentList.map {
                if (it.id == comment.id) {
                    val nowLiked = !it.likedByMe
                    val delta = if (nowLiked) 1 else -1
                    it.copy(
                        likedByMe = nowLiked,
                        likeCount = (it.likeCount + delta).coerceAtLeast(0)
                    )
                } else it
            }

            try {
                repo.toggleCommentLike(postId, comment.id)
            } catch (e: Exception) {
                // nếu fail thì reload lại list từ server
                observeComments()
                _error.value = e.message
            }
        }
    }

    // ============ SEND COMMENT ============

    fun sendComment() {
        val text = commentText.value.trim()
        val medias = commentMediaUris.value
        val type = commentMediaType.value
        val replyTarget = replyingTo.value
        val editing = editingComment.value

        // nếu đang chỉnh sửa: chỉ update text, không tạo comment mới
        if (editing != null) {
            if (text.isEmpty()) return
            if (_sending.value) return

            viewModelScope.launch {
                _sending.value = true
                try {
                    repo.editCommentText(postId, editing.id, text)
                    _commentText.value = ""
                    _editingComment.value = null
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _sending.value = false
                }
            }
            return
        }

        // Không cho gửi nếu không có text và cũng không có media
        if (text.isEmpty() && medias.isEmpty()) return
        if (_sending.value) return

        // Tối đa 2 tầng: comment gốc + reply
        val parentId: String? = when {
            replyTarget == null -> null
            replyTarget.parentCommentId.isNullOrEmpty() -> replyTarget.id
            else -> replyTarget.parentCommentId
        }

        viewModelScope.launch {
            _sending.value = true
            try {
                repo.addComment(
                    postId = postId,
                    text = text,
                    parentCommentId = parentId,
                    mediaUris = medias,
                    mediaType = type
                )

                // clear input sau khi gửi
                _commentText.value = ""
                clearMedia()
                clearReply()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _sending.value = false
            }
        }
    }
}
