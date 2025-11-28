package com.example.uth_hub.feature.profile.data

import android.graphics.Bitmap
import android.net.Uri
import com.example.uth_hub.feature.auth.AuthConst
import com.example.uth_hub.feature.auth.domain.model.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class ProfileRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    private val storage = FirebaseStorage.getInstance()

    private fun userDoc() =
        db.collection(AuthConst.USERS).document(auth.currentUser?.uid ?: "")

    /** Lấy realtime hồ sơ user hiện tại (Flow) */
    fun currentUserFlow(): Flow<AppUser?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null); close(); return@callbackFlow
        }
        val reg = userDoc().addSnapshotListener { s, e ->
            if (e != null) {
                trySend(null); return@addSnapshotListener
            }
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

    /** Upload avatar từ Uri (thư viện) và cập nhật photoUrl */
    suspend fun updateAvatarFromUri(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        val ref = storage.reference.child("avatars/$uid/avatar.jpg")

        ref.putFile(uri).await()
        val url = ref.downloadUrl.await().toString()

        userDoc().update(
            mapOf(
                "photoUrl" to url,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    /** Upload avatar từ Bitmap (chụp ảnh) và cập nhật photoUrl */
    suspend fun updateAvatarFromBitmap(bitmap: Bitmap) {
        val uid = auth.currentUser?.uid ?: return
        val ref = storage.reference.child("avatars/$uid/avatar.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val data = baos.toByteArray()

        ref.putBytes(data).await()
        val url = ref.downloadUrl.await().toString()

        userDoc().update(
            mapOf(
                "photoUrl" to url,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    /** Đưa avatar về mặc định (avatar Google hiện tại của account) */
    suspend fun resetAvatarToGoogleDefault() {
        val uid = auth.currentUser?.uid ?: return
        val googleUrl = auth.currentUser?.photoUrl?.toString() ?: ""

        userDoc().update(
            mapOf(
                "photoUrl" to googleUrl,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }
    suspend fun updateUserProfile(
        mssv: String,
        phone: String,
        institute: String,
        classCode: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(uid)
            .update(
                mapOf(
                    "mssv" to mssv,
                    "phone" to phone,
                    "institute" to institute,
                    "classCode" to classCode,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }

    fun signOut() = auth.signOut()
}
