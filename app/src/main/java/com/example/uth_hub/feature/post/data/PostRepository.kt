package com.example.uth_hub.feature.post.data

import android.net.Uri
import com.example.uth_hub.core.notification.NotificationSender
import com.example.uth_hub.feature.post.domain.model.PostModel
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PostRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    private val postsCol get() = db.collection("posts")

    // ========================
    // CREATE POST
    // ========================
    suspend fun createPost(
        content: String,
        imageUris: List<Uri>,
        videoUri: Uri? = null,
        authorName: String,
        authorHandle: String,
        authorInstitute: String,
        authorAvatarUrl: String
    ): String {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        val imageUrls = mutableListOf<String>()
        for (uri in imageUris) {
            val fileName = "posts/$uid/${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val ref = storage.reference.child(fileName)
            ref.putFile(uri).await()
            imageUrls += ref.downloadUrl.await().toString()
        }

        val doc = postsCol.document()
        val data = hashMapOf(
            "authorId" to uid,
            "authorName" to authorName,
            "authorHandle" to authorHandle,
            "authorInstitute" to authorInstitute,
            "authorAvatarUrl" to authorAvatarUrl,
            "content" to content,
            "imageUrls" to imageUrls,
            "createdAt" to FieldValue.serverTimestamp(),
            "likeCount" to 0L,
            "commentCount" to 0L,
            "saveCount" to 0L,
            // 🔹 mới: số lần bị báo cáo (cho admin xem)
            "reportCount" to 0L
            // "videoUrl" nếu sau này cần có thể set thêm ở đây
        )
        doc.set(data).await()
        return doc.id
    }

    fun feedQuery(limit: Long = 30): Query =
        postsCol.orderBy("createdAt", Query.Direction.DESCENDING).limit(limit)

    // ========================
    // CHECK LIKE / SAVE
    // ========================
    suspend fun isLiked(postId: String, uid: String): Boolean {
        val doc = postsCol.document(postId).collection("likes").document(uid).get().await()
        return doc.exists()
    }

    suspend fun isSaved(postId: String, uid: String): Boolean {
        val doc = postsCol.document(postId).collection("saves").document(uid).get().await()
        return doc.exists()
    }

    // ========================
    // REPORT POST (BÁO CÁO VI PHẠM)
    // ========================
    /**
     * Báo cáo 1 bài viết.
     *
     * - Lưu dấu trong subcollection: posts/{postId}/reports/{uid}
     * - Mỗi user chỉ tăng reportCount đúng 1 lần cho mỗi post.
     *
     * @return true  -> lần đầu user này báo cáo post này
     *         false -> user đã báo cáo post này trước đó, không tăng reportCount nữa
     */
    suspend fun reportPost(postId: String): Boolean {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        val postDoc = postsCol.document(postId)
        val reportDoc = postDoc.collection("reports").document(uid)

        return db.runTransaction { tr ->
            val reported = tr.get(reportDoc).exists()

            if (reported) {
                // Đã từng báo cáo rồi -> không làm gì
                false
            } else {
                // Lần đầu báo cáo -> tạo doc + tăng reportCount
                tr.set(
                    reportDoc,
                    mapOf("createdAt" to FieldValue.serverTimestamp())
                )
                tr.update(postDoc, "reportCount", FieldValue.increment(1))
                true
            }
        }.await()
    }

    // ========================
    //  LIKE — FIXED (có authorId)
    // ========================
    suspend fun toggleLike(postId: String, postAuthorId: String) {
        val uid = auth.currentUser?.uid ?: return

        val likeDoc = postsCol.document(postId).collection("likes").document(uid)
        val postDoc = postsCol.document(postId)

        // chạy transaction, trả về true nếu là hành động LIKE, false nếu UNLIKE
        val justLiked = db.runTransaction { tr ->
            val liked = tr.get(likeDoc).exists()

            if (liked) {
                // Unlike
                tr.delete(likeDoc)
                tr.update(postDoc, "likeCount", FieldValue.increment(-1))
                false   // vừa UNLIKE
            } else {
                // Like
                tr.set(likeDoc, mapOf("createdAt" to FieldValue.serverTimestamp()))
                tr.update(postDoc, "likeCount", FieldValue.increment(1))
                true    // vừa LIKE
            }
        }.await()

        // 🔥 Gửi thông báo SAU KHI transaction hoàn tất
        if (justLiked && uid != postAuthorId) {
            NotificationSender.sendLikeNotification(
                postId = postId,
                receiverId = postAuthorId
            )
        }
    }

    // ========================
    // SAVE
    // ========================
    suspend fun toggleSave(postId: String) {
        val uid = auth.currentUser?.uid ?: return

        val postDoc = postsCol.document(postId)
        val postSaveDoc = postDoc.collection("saves").document(uid)
        val userSaveDoc = db.collection("users").document(uid).collection("saves").document(postId)

        db.runTransaction { tr ->
            val saved = tr.get(userSaveDoc).exists()
            if (saved) {
                tr.delete(userSaveDoc)
                tr.delete(postSaveDoc)
                tr.update(postDoc, "saveCount", FieldValue.increment(-1))
            } else {
                val payload = mapOf("createdAt" to FieldValue.serverTimestamp())
                tr.set(userSaveDoc, payload)
                tr.set(postSaveDoc, payload)
                tr.update(postDoc, "saveCount", FieldValue.increment(1))
            }
        }.await()
    }

    // ========================
    // LIKE COMMENT
    // ========================

    suspend fun toggleCommentLike(postId: String, commentId: String) {
        val uid = auth.currentUser?.uid ?: return

        val commentDoc = postsCol.document(postId)
            .collection("comments").document(commentId)
        val likeDoc = commentDoc.collection("likes").document(uid)

        db.runTransaction { tr ->
            val liked = tr.get(likeDoc).exists()

            if (liked) {
                tr.delete(likeDoc)
                tr.update(commentDoc, "likeCount", FieldValue.increment(-1))
            } else {
                tr.set(likeDoc, mapOf("createdAt" to FieldValue.serverTimestamp()))
                tr.update(commentDoc, "likeCount", FieldValue.increment(1))
            }
        }.await()
    }

    // ========================
    // SAVED ID STREAM
    // ========================
    fun savedIdsFlow(uid: String) = callbackFlow<Set<String>> {
        val ref = db.collection("users").document(uid).collection("saves")
        val reg = ref.addSnapshotListener { snap, _ ->
            val ids = snap?.documents?.map { it.id }?.toSet() ?: emptySet()
            trySend(ids)
        }
        awaitClose { reg.remove() }
    }

    // ========================
    // POST => MODEL
    // ========================
    private fun docToPost(d: com.google.firebase.firestore.DocumentSnapshot): PostModel =
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
            videoUrl = d.getString("videoUrl") ?: "",
            createdAt = d.getTimestamp("createdAt"),
            likeCount = d.getLong("likeCount") ?: 0L,
            commentCount = d.getLong("commentCount") ?: 0L,
            saveCount = d.getLong("saveCount") ?: 0L,
            // 🔹 map thêm reportCount từ Firestore
            reportCount = d.getLong("reportCount") ?: 0L
        )

    suspend fun getPostById(postId: String): PostModel? {
        val doc = postsCol.document(postId).get().await()
        return if (doc.exists()) docToPost(doc) else null
    }

    // ========================
    // COMMENT => MODEL
    // ========================
    private fun docToComment(
        postId: String,
        d: com.google.firebase.firestore.DocumentSnapshot
    ): CommentModel =
        CommentModel(
            id = d.id,
            postId = postId,
            authorId = d.getString("authorId") ?: "",
            authorName = d.getString("authorName") ?: "",
            authorAvatarUrl = d.getString("authorAvatarUrl") ?: "",
            text = d.getString("text") ?: "",
            createdAt = d.getTimestamp("createdAt"),
            parentCommentId = d.getString("parentCommentId"),
            likeCount = d.getLong("likeCount") ?: 0L,
            replyCount = d.getLong("replyCount") ?: 0L,
            mediaUrls = (d.get("mediaUrls") as? List<*>)?.filterIsInstance<String>()
                ?: emptyList(),
            mediaType = d.getString("mediaType"),
            likedByMe = false
        )

    // ========================
    // OBSERVE COMMENTS
    // ========================
    fun observeComments(postId: String): Flow<List<CommentModel>> = callbackFlow {
        val ref = postsCol.document(postId)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)

        val reg = ref.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
            } else {
                trySend(snap?.documents?.map { docToComment(postId, it) } ?: emptyList())
            }
        }
        awaitClose { reg.remove() }
    }

    // ========================
    // ADD COMMENT
    // ========================
    suspend fun addComment(
        postId: String,
        text: String,
        parentCommentId: String? = null,
        mediaUris: List<Uri> = emptyList(),
        mediaType: String? = null
    ) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        val userDoc = db.collection("users").document(uid).get().await()
        val authorName = userDoc.getString("displayName")
            ?: userDoc.getString("name")
            ?: auth.currentUser?.displayName
            ?: "Ẩn danh"

        val avatarUrl = userDoc.getString("photoUrl")
            ?: userDoc.getString("avatarUrl")
            ?: auth.currentUser?.photoUrl?.toString()
            ?: ""

        val postDoc = postsCol.document(postId)
        val commentsCol = postDoc.collection("comments")
        val newCommentRef = commentsCol.document()

        val mediaUrls = mutableListOf<String>()
        for (uri in mediaUris) {
            val fileName =
                "comments/$postId/${newCommentRef.id}/${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val ref = storage.reference.child(fileName)
            ref.putFile(uri).await()
            mediaUrls += ref.downloadUrl.await().toString()
        }

        val data = mutableMapOf<String, Any?>(
            "authorId" to uid,
            "authorName" to authorName,
            "authorAvatarUrl" to avatarUrl,
            "text" to text,
            "createdAt" to FieldValue.serverTimestamp(),
            "parentCommentId" to parentCommentId,
            "likeCount" to 0L,
            "replyCount" to 0L,
            "mediaUrls" to mediaUrls,
            "mediaType" to mediaType
        )

        db.runTransaction { tr ->
            tr.set(newCommentRef, data)
            // mỗi comment (kể cả reply) đều +1 commentCount
            tr.update(postDoc, "commentCount", FieldValue.increment(1))

            if (!parentCommentId.isNullOrEmpty()) {
                tr.update(
                    commentsCol.document(parentCommentId),
                    "replyCount", FieldValue.increment(1)
                )
            }
        }.await()

        // ===== 🔥 TẠO THÔNG BÁO COMMENT (UI Notification) =====
        val postSnapshot = postDoc.get().await()
        val postAuthorId = postSnapshot.getString("authorId")

        if (postAuthorId != null && postAuthorId != uid) {

            val notiRef = db.collection("notifications").document()

            val notiData = mapOf(
                "id" to notiRef.id,
                "type" to "comment",
                "postId" to postId,
                "senderId" to uid,
                "senderName" to authorName,
                "senderAvatar" to avatarUrl,
                "commentContent" to text,
                "message" to "$authorName đã bình luận bài viết của bạn",   // 👈 giống like
                "receiverId" to postAuthorId,
                "timestamp" to FieldValue.serverTimestamp(),       // 👈 đồng bộ với like
                "isRead" to false
            )

            notiRef.set(notiData)
        }
    }

    // ========================
    // EDIT COMMENT
    // ========================
    suspend fun editCommentText(postId: String, commentId: String, newText: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        val commentRef = postsCol.document(postId)
            .collection("comments").document(commentId)

        db.runTransaction { tr ->
            val snap = tr.get(commentRef)
            if (!snap.exists()) throw IllegalStateException("Comment not found")

            if (snap.getString("authorId") != uid)
                throw IllegalStateException("Không có quyền chỉnh sửa bình luận này")

            tr.update(
                commentRef,
                mapOf(
                    "text" to newText,
                    "editedAt" to FieldValue.serverTimestamp()
                )
            )
        }.await()
    }

    // ========================
    // DELETE COMMENT
    // ========================
    suspend fun deleteComment(postId: String, comment: CommentModel) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        val postDoc = postsCol.document(postId)
        val commentsCol = postDoc.collection("comments")
        val commentRef = commentsCol.document(comment.id)

        db.runTransaction { tr ->
            val snap = tr.get(commentRef)
            if (!snap.exists()) return@runTransaction

            if (snap.getString("authorId") != uid)
                throw IllegalStateException("Không có quyền xoá bình luận này")

            // dùng replyCount trên server cho chắc
            val replyCountOnServer = snap.getLong("replyCount") ?: 0L
            val totalDec = 1L + replyCountOnServer
            tr.update(postDoc, "commentCount", FieldValue.increment(-totalDec))

            val parentId = snap.getString("parentCommentId")
            if (!parentId.isNullOrEmpty()) {
                tr.update(
                    commentsCol.document(parentId),
                    "replyCount", FieldValue.increment(-1)
                )
            }

            tr.delete(commentRef)
        }.await()

        // XOÁ reply + likes
        commentsCol.whereEqualTo("parentCommentId", comment.id)
            .get().await().documents.forEach { replyDoc ->
                // xoá likes của từng reply
                replyDoc.reference.collection("likes")
                    .get().await().documents.forEach { it.reference.delete().await() }
                replyDoc.reference.delete().await()
            }

        commentRef.collection("likes")
            .get().await().documents.forEach { it.reference.delete().await() }
    }

    // ========================
    // FETCH POSTS BY REFERNECE
    // ========================
    private suspend fun fetchPostsByRefs(postRefs: List<DocumentReference>): List<PostModel> {
        if (postRefs.isEmpty()) return emptyList()

        val snaps = postRefs.map { it.get().await() }
        return snaps.filter { it.exists() }
            .map { docToPost(it) }
            .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
    }

    // ========================
    // GET LIKED POSTS
    // ========================
    suspend fun getLikedPostsByMe(limit: Long = 50): List<PostModel> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val snap = postsCol.orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit).get().await()

        val posts = snap.documents.map { docToPost(it) }

        return posts.filter { isLiked(it.id, uid) }
            .map { it.copy(likedByMe = true) }
    }

    // ========================
    // GET SAVED POSTS
    // ========================
    suspend fun getSavedPostsByMe(limit: Long = 50): List<PostModel> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val snap = postsCol.orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit).get().await()

        val posts = snap.documents.map { docToPost(it) }

        return posts.filter { isSaved(it.id, uid) }
            .map { it.copy(savedByMe = true) }
    }

    // ========================
    // DELETE POST (AUTHOR OR ADMIN)
    // ========================
    suspend fun deletePost(postId: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        val doc = postsCol.document(postId).get().await()
        if (!doc.exists()) return

        val authorId = doc.getString("authorId")
        val isAdmin = verifyAdminAccess()

        if (uid != authorId && !isAdmin) {
            throw IllegalStateException("Bạn không có quyền xoá bài viết này")
        }

        doc.reference.delete().await()
    }

    // ========================
    // DELETE POST (TÁC GIẢ TỰ XÓA)
    // ========================
    suspend fun deleteMyPost(postId: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        val docRef = postsCol.document(postId)
        val snap = docRef.get().await()
        val authorId = snap.getString("authorId")

        if (authorId != uid) {
            throw IllegalStateException("Bạn không thể xoá bài viết của người khác")
        }

        // Firestore rules cũng sẽ check lại authorId == request.auth.uid
        docRef.delete().await()
    }

    private suspend fun verifyAdminAccess(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val doc = db.collection("users").document(uid).get().await()
        return doc.getString("role") == "admin"
    }
}
