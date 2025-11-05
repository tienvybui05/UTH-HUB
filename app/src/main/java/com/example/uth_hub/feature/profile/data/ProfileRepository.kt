package com.example.uth_hub.feature.profile.data

import com.example.uth_hub.feature.auth.AuthConst
import com.example.uth_hub.feature.auth.domain.model.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    private fun userDoc() =
        db.collection(AuthConst.USERS).document(auth.currentUser?.uid ?: "")

    /** Lấy realtime hồ sơ user hiện tại (Flow) */
    fun currentUserFlow(): Flow<AppUser?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null); close(); return@callbackFlow
        }
        val reg = userDoc().addSnapshotListener { s, e ->
            if (e != null) { trySend(null); return@addSnapshotListener }
            trySend(s?.toObject(AppUser::class.java)?.copy(uid = uid))
        }
        awaitClose { reg.remove() }
    }

    /** Cập nhật các trường hồ sơ cơ bản */
    suspend fun updateBasic(
        displayName: String? = null,
        mssv: String? = null,
        phone: String? = null,
        major: String? = null,
        classCode: String? = null
    ) {
        val map = buildMap<String, Any> {
            displayName?.let { put("displayName", it) }
            mssv?.let { put("mssv", it) }
            phone?.let { put("phone", it) }
            major?.let { put("major", it) }
            classCode?.let { put("classCode", it) }
            put("updatedAt", System.currentTimeMillis())
        }
        if (map.isNotEmpty()) userDoc().update(map).await()
    }

    fun signOut() = auth.signOut()
}
