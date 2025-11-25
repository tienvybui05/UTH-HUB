package com.example.uth_hub.feature.post.data

import android.net.Uri
import com.example.uth_hub.feature.post.domain.model.PostModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.example.uth_hub.feature.post.domain.model.CommentModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class PostRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val postsCol get() = db.collection("posts")

    suspend fun createPost(
        content: String,
        imageUris: List<Uri>,
        authorName: String,
        authorHandle: String,
        authorInstitute: String,
        authorAvatarUrl: String
    ): String {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        // Nếu chưa bật Storage: hãy truyền imageUris = emptyList() khi gọi hàm
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
            "saveCount" to 0L
        )
        doc.set(data).await()
        return doc.id
    }

    fun feedQuery(limit: Long = 30): Query =
        postsCol.orderBy("createdAt", Query.Direction.DESCENDING).limit(limit)

    suspend fun isLiked(postId: String, uid: String): Boolean {
        val doc = postsCol.document(postId).collection("likes").document(uid).get().await()
        return doc.exists()
    }

    suspend fun isSaved(postId: String, uid: String): Boolean {
        val doc = postsCol.document(postId).collection("saves").document(uid).get().await()
        return doc.exists()
    }

    suspend fun toggleLike(postId: String) {
        val uid = auth.currentUser?.uid ?: return
        val likeDoc = postsCol.document(postId).collection("likes").document(uid)
        val postDoc = postsCol.document(postId)

        db.runTransaction { tr ->
            val liked = tr.get(likeDoc).exists()
            if (liked) {
                tr.delete(likeDoc)
                tr.update(postDoc, "likeCount", FieldValue.increment(-1))
            } else {
                tr.set(likeDoc, mapOf("createdAt" to FieldValue.serverTimestamp()))
                tr.update(postDoc, "likeCount", FieldValue.increment(1))
            }
        }.await()
    }

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

    // === SAVED IDS STREAM (nếu cần) ===
    fun savedIdsFlow(uid: String) = callbackFlow<Set<String>> {
        val ref = db.collection("users").document(uid).collection("saves")
        val reg = ref.addSnapshotListener { snap, _ ->
            val ids: Set<String> = snap?.documents?.map { it.id }?.toSet() ?: emptySet()
            trySend(ids)
        }
        awaitClose { reg.remove() }
    }

    // === Helpers ===
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
                ?: emptyList(), // dùng emptyList<String>() mặc định Kotlin
            createdAt = d.getTimestamp("createdAt"),
            likeCount = d.getLong("likeCount") ?: 0L,
            commentCount = d.getLong("commentCount") ?: 0L,
            saveCount = d.getLong("saveCount") ?: 0L
        )

    suspend fun getPostById(postId: String): PostModel? {
        val doc = postsCol.document(postId).get().await()
        return if (doc.exists()) docToPost(doc) else null
    }

    //Helper chuyển DocumentSnapshot → CommentModel
    private fun docToComment(postId: String, d: com.google.firebase.firestore.DocumentSnapshot): CommentModel =
        CommentModel(
            id = d.id,
            postId = postId,
            authorId = d.getString("authorId") ?: "",
            authorName = d.getString("authorName") ?: "",
            authorAvatarUrl = d.getString("authorAvatarUrl") ?: "",
            text = d.getString("text") ?: "",
            createdAt = d.getTimestamp("createdAt")
        )

    //Flow listen comments của 1 post

    fun observeComments(postId: String): Flow<List<CommentModel>> = callbackFlow {
        val ref = postsCol.document(postId)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)

        val reg = ref.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
            } else {
                val list = snap?.documents
                    ?.map { docToComment(postId, it) }
                    ?: emptyList()
                trySend(list)
            }
        }
        awaitClose { reg.remove() }
    }

    // thêm comment
    suspend fun addComment(postId: String, text: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        // Lấy thông tin user để gắn vào comment
        val userDoc = db.collection("users").document(uid).get().await()
        val authorName = userDoc.getString("displayName")
            ?: userDoc.getString("name")
            ?: auth.currentUser?.displayName
            ?: "Ẩn danh"

        val avatarUrl = userDoc.getString("photoUrl")
            ?: userDoc.getString("avatarUrl")
            ?: auth.currentUser?.photoUrl?.toString()
            ?: ""

        val commentsCol = postsCol.document(postId).collection("comments")

        val data = mapOf(
            "authorId" to uid,
            "authorName" to authorName,
            "authorAvatarUrl" to avatarUrl,
            "text" to text,
            "createdAt" to FieldValue.serverTimestamp()
        )

        // Tạo comment + tăng commentCount
        val postDoc = postsCol.document(postId)
        db.runTransaction { tr ->
            tr.set(commentsCol.document(), data)
            tr.update(postDoc, "commentCount", FieldValue.increment(1))
        }.await()
    }




    private suspend fun fetchPostsByRefs(postRefs: List<DocumentReference>): List<PostModel> {
        if (postRefs.isEmpty()) return emptyList()
        // Dùng từng get().await() để tránh lỗi getAll() trên vài phiên bản lib
        val snaps = postRefs.map { it.get().await() }
        return snaps
            .filter { it.exists() }
            .map { docToPost(it) }
            .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
    }

    // === Liked & Saved feeds ===

//    suspend fun getLikedPostsByMe(limit: Long = 50): List<PostModel> {
//        val uid = auth.currentUser?.uid ?: return emptyList()
//        val likeDocs = db.collectionGroup("likes")
//            .whereEqualTo(FieldPath.documentId(), uid)
//            .limit(limit)
//            .get()
//            .await()
//
//        val postRefs = likeDocs.documents.mapNotNull { it.reference.parent.parent } // /posts/{postId}
//        val posts = fetchPostsByRefs(postRefs)
//        return posts.map { it.copy(likedByMe = true) }
//    }
suspend fun getLikedPostsByMe(limit: Long = 50): List<PostModel> {
    val uid = auth.currentUser?.uid ?: return emptyList()

    // 1. Lấy một list post mới nhất
    val snap = postsCol
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(limit)
        .get()
        .await()

    val allPosts = snap.documents.map { docToPost(it) }

    // 2. Chỉ giữ lại những post mà chính mình đã like
    val result = mutableListOf<PostModel>()
    for (p in allPosts) {
        val liked = isLiked(p.id, uid)   // đọc /posts/{postId}/likes/{uid}
        if (liked) {
            result += p.copy(likedByMe = true)
        }
    }
    return result
}


//    suspend fun getSavedPostsByMe(limit: Long = 50): List<PostModel> {
//        val uid = auth.currentUser?.uid ?: return emptyList()
//        val saveDocs = db.collectionGroup("saves")
//            .whereEqualTo(FieldPath.documentId(), uid)
//            .limit(limit)
//            .get()
//            .await()
//
//        val postRefs = saveDocs.documents.mapNotNull { it.reference.parent.parent }
//        val posts = fetchPostsByRefs(postRefs)
//        return posts.map { it.copy(savedByMe = true) }
//    }
suspend fun getSavedPostsByMe(limit: Long = 50): List<PostModel> {
    val uid = auth.currentUser?.uid ?: return emptyList()

    // 1. Lấy một list post mới nhất
    val snap = postsCol
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(limit)
        .get()
        .await()

    val allPosts = snap.documents.map { docToPost(it) }

    // 2. Chỉ giữ lại những post mà chính mình đã lưu
    val result = mutableListOf<PostModel>()
    for (p in allPosts) {
        val saved = isSaved(p.id, uid)   // đọc /posts/{postId}/likes/{uid}
        if (saved) {
            result += p.copy(savedByMe = true)
        }
    }
    return result
}
    suspend fun deletePost(postId: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

        // Kiểm tra admin status
        val isAdmin = verifyAdminAccess()
        if (!isAdmin) {
            throw IllegalStateException("User $uid is not admin. Cannot delete post $postId")
        }

        println("✅ CONFIRMED: User is admin. Deleting post $postId")

        val postDoc = postsCol.document(postId)

        // Xóa tất cả dữ liệu liên quan
        db.runTransaction { tr ->
            val post = tr.get(postDoc)
            tr.delete(postDoc)

            val imageUrls = post.get("imageUrls") as? List<String> ?: emptyList()
            if (imageUrls.isNotEmpty()) {
                println("Có ${imageUrls.size} ảnh sẽ xóa sau")
            }
        }.await()

        // Xóa ảnh sau khi transaction hoàn thành
        deletePostImages(postId)

        // Xóa các subcollections và saved posts
        deletePostSubcollections(postId)

        println("✅ Đã xóa bài viết $postId thành công")
    }

    // Hàm kiểm tra admin


    // Thêm hàm verifyAdminAccess
    private suspend fun verifyAdminAccess(): Boolean {
        val uid = auth.currentUser?.uid ?: return false

        println("=== VERIFYING ADMIN ACCESS ===")
        println("User UID: $uid")

        try {
            val userDoc = db.collection("users").document(uid).get().await()
            val role = userDoc.getString("role")
            val isAdmin = role == "admin"

            println("User role: $role")
            println("Is admin: $isAdmin")
            println("============================")

            return isAdmin
        } catch (e: Exception) {
            println("❌ Error checking admin: ${e.message}")
            return false
        }
    }

    // Hàm riêng để xóa ảnh
    private suspend fun deletePostImages(postId: String) {
        try {
            val postDoc = postsCol.document(postId).get().await()
            if (postDoc.exists()) {
                val imageUrls = postDoc.get("imageUrls") as? List<String> ?: emptyList()
                imageUrls.forEach { url ->
                    try {
                        storage.getReferenceFromUrl(url).delete().await()
                    } catch (e: Exception) {
                        println("Lỗi xóa ảnh $url: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("Lỗi khi lấy thông tin post để xóa ảnh: ${e.message}")
        }
    }

    // Hàm riêng để xóa subcollections
    private suspend fun deletePostSubcollections(postId: String) {
        val postDoc = postsCol.document(postId)

        // Xóa các subcollections
        val subcollections = listOf("likes", "saves", "comments", "reports")

        subcollections.forEach { collectionName ->
            try {
                val snapshot = postDoc.collection(collectionName).get().await()
                snapshot.documents.forEach { doc ->
                    doc.reference.delete().await()
                }
            } catch (e: Exception) {
                println("Lỗi xóa collection $collectionName: ${e.message}")
            }
        }

        // Xóa từ saved posts của tất cả users
        try {
            val savesQuery = db.collectionGroup("saves").whereEqualTo(FieldPath.documentId(), postId)
            val saveDocs = savesQuery.get().await()
            saveDocs.documents.forEach { doc ->
                doc.reference.delete().await()
            }
        } catch (e: Exception) {
            println("Lỗi xóa saved posts: ${e.message}")
        }
    }
}
