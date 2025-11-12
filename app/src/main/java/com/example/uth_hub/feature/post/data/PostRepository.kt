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

    suspend fun getLikedPostsByMe(limit: Long = 50): List<PostModel> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val likeDocs = db.collectionGroup("likes")
            .whereEqualTo(FieldPath.documentId(), uid)
            .limit(limit)
            .get()
            .await()

        val postRefs = likeDocs.documents.mapNotNull { it.reference.parent.parent } // /posts/{postId}
        val posts = fetchPostsByRefs(postRefs)
        return posts.map { it.copy(likedByMe = true) }
    }

    suspend fun getSavedPostsByMe(limit: Long = 50): List<PostModel> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val saveDocs = db.collectionGroup("saves")
            .whereEqualTo(FieldPath.documentId(), uid)
            .limit(limit)
            .get()
            .await()

        val postRefs = saveDocs.documents.mapNotNull { it.reference.parent.parent }
        val posts = fetchPostsByRefs(postRefs)
        return posts.map { it.copy(savedByMe = true) }
    }
}
