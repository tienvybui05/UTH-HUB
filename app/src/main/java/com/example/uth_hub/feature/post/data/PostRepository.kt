package com.example.uth_hub.feature.post.data

import android.net.Uri
import com.example.uth_hub.feature.post.domain.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
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

        // 1) Upload ảnh (nếu có)
        val imageUrls = mutableListOf<String>()
        for (uri in imageUris) {
            val fileName = "posts/${uid}/${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val ref = storage.reference.child(fileName)
            ref.putFile(uri).await()
            imageUrls += ref.downloadUrl.await().toString()
        }

        // 2) Tạo doc post
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
        val saveDoc = postsCol.document(postId).collection("saves").document(uid)
        val postDoc = postsCol.document(postId)

        db.runTransaction { tr ->
            val saved = tr.get(saveDoc).exists()
            if (saved) {
                tr.delete(saveDoc)
                tr.update(postDoc, "saveCount", FieldValue.increment(-1))
            } else {
                tr.set(saveDoc, mapOf("createdAt" to FieldValue.serverTimestamp()))
                tr.update(postDoc, "saveCount", FieldValue.increment(1))
            }
        }.await()
    }
}
