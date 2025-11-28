package com.example.uth_hub.feature.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.post.data.PostRepository
import com.example.uth_hub.feature.post.domain.model.PostModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repo: PostRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostModel>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(true) // ← Thêm trạng thái loading
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var uid: String? = auth.currentUser?.uid

    init {
        observeFeed()
    }

    private fun observeFeed() {
        val q: Query = repo.feedQuery()

        // Bắt đầu loading
        _isLoading.value = true

        callbackFlow {
            val reg = q.addSnapshotListener { snap, err ->
                if (err != null) {
                    trySend(Result.failure(err))
                } else {
                    val raw = snap?.documents?.map { d ->
                        PostModel(
                            id = d.id,
                            authorId = d.getString("authorId") ?: "",
                            authorName = d.getString("authorName") ?: "",
                            authorHandle = d.getString("authorHandle") ?: "",
                            authorInstitute = d.getString("authorInstitute") ?: "",
                            authorAvatarUrl = d.getString("authorAvatarUrl") ?: "",
                            content = d.getString("content") ?: "",
                            imageUrls = (d.get("imageUrls") as? List<*>)?.filterIsInstance<String>()
                                ?: emptyList(),
                            createdAt = d.getTimestamp("createdAt"),
                            likeCount = d.getLong("likeCount") ?: 0L,
                            commentCount = d.getLong("commentCount") ?: 0L,
                            saveCount = d.getLong("saveCount") ?: 0L,
                            // 🔥 THÊM DÒNG NÀY - reportCount bị thiếu!
                            reportCount = d.getLong("reportCount") ?: 0L
                        )
                    } ?: emptyList()
                    trySend(Result.success(raw))
                }
            }
            awaitClose { reg.remove() }
        }
            .onEach { result ->
                result.fold(
                    onSuccess = { list ->
                        viewModelScope.launch {
                            _posts.value = enrichWithFlags(list)
                            _isLoading.value = false
                        }
                    },
                    onFailure = { e ->
                        _error.value = (e as? FirebaseFirestoreException)?.message ?: e.message
                        _isLoading.value = false
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    /**
     * Thêm cờ likedByMe / savedByMe cho mỗi post (chạy song song để nhanh).
     */
    private suspend fun enrichWithFlags(list: List<PostModel>): List<PostModel> = coroutineScope {
        val current = uid ?: return@coroutineScope list // chưa đăng nhập => giữ nguyên
        list.map { p ->
            async {
                val liked = repo.isLiked(p.id, current)
                val saved = repo.isSaved(p.id, current)
                p.copy(likedByMe = liked, savedByMe = saved)
            }
        }.map { it.await() }
    }

    fun refreshUser() {
        uid = auth.currentUser?.uid
    }

    /**
     * Optimistic update: cập nhật UI ngay, sau đó gọi Firestore.
     */
    fun toggleLike(postId: String, postAuthorId: String)
    {
        // 1) UI trước
        _posts.value = _posts.value.map { p ->
            if (p.id == postId) {
                val nowLiked = !p.likedByMe
                p.copy(
                    likedByMe = nowLiked,
                    likeCount = (p.likeCount + if (nowLiked) 1 else -1).coerceAtLeast(0)
                )
            } else p
        }
        // 2) Gọi repo
        viewModelScope.launch {
            try {
                repo.toggleLike(postId, postAuthorId)
            } catch (e: Exception) {
                // rollback nếu lỗi
                _posts.value = _posts.value.map { p ->
                    if (p.id == postId) {
                        val nowLiked = !p.likedByMe
                        p.copy(
                            likedByMe = nowLiked,
                            likeCount = (p.likeCount + if (nowLiked) 1 else -1).coerceAtLeast(0)
                        )
                    } else p
                }
                _error.value = e.message
            }
        }
    }

    fun toggleSave(postId: String) {
        // 1) UI trước
        _posts.value = _posts.value.map { p ->
            if (p.id == postId) {
                val nowSaved = !p.savedByMe
                p.copy(
                    savedByMe = nowSaved,
                    saveCount = (p.saveCount + if (nowSaved) 1 else -1).coerceAtLeast(0)
                )
            } else p
        }
        // 2) Gọi repo
        viewModelScope.launch {
            try {
                repo.toggleSave(postId)
            } catch (e: Exception) {
                // rollback nếu lỗi
                _posts.value = _posts.value.map { p ->
                    if (p.id == postId) {
                        val nowSaved = !p.savedByMe
                        p.copy(
                            savedByMe = nowSaved,
                            saveCount = (p.saveCount + if (nowSaved) 1 else -1).coerceAtLeast(0)
                        )
                    } else p
                }
                _error.value = e.message
            }
        }
    }

    fun deletePost(postId: String) {
        // Lưu toàn bộ state hiện tại để rollback
        val currentPosts = _posts.value
        // Filter để loại bỏ post cần xóa
        _posts.value = currentPosts.filter { it.id != postId }

        viewModelScope.launch {
            try {
                println("🔄 Bắt đầu xóa bài viết: $postId")
                repo.deletePost(postId)
                println("✅ Xóa bài viết $postId thành công từ ViewModel")
            } catch (e: Exception) {
                println("❌ Lỗi khi xóa bài viết $postId: ${e.message}")
                e.printStackTrace()

                // Rollback: khôi phục state trước khi xóa
                _posts.value = currentPosts
                println("🔄 Đã rollback bài viết $postId")
                _error.value = "Lỗi khi xóa bài viết: ${e.message}"
            }
        }
    }

    // Hàm để refresh feed (nếu cần)
    fun loadPosts() {
        _isLoading.value = true // ← Bắt đầu loading khi refresh
        observeFeed()
    }

    // Hàm để set loading thủ công (nếu cần)
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}