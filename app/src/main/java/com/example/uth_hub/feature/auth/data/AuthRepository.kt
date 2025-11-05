package com.example.uth_hub.feature.auth.data

import android.app.Activity
import android.content.Context
import com.example.uth_hub.feature.auth.AuthConst
import com.example.uth_hub.feature.auth.domain.model.AppUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    /** Tạo GoogleSignInClient */
    fun buildGoogleClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(
                // lấy web client id từ google-services.json (default_web_client_id)
                com.example.uth_hub.R.string.default_web_client_id
            ))
            .requestEmail()
            .setHostedDomain("ut.edu.vn") // <-- chỉ là hint, có thể bị bỏ qua
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    /** Đăng nhập với Google (đã lấy được account từ ActivityResult) */
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Pair<Boolean, AppUser> {
        val email = account.email ?: ""
        require(email.endsWith(AuthConst.UTH_DOMAIN)) {
            "Email phải có đuôi ${AuthConst.UTH_DOMAIN}"
        }

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val isNew = result.additionalUserInfo?.isNewUser == true
        val user = auth.currentUser ?: throw IllegalStateException("No user")

        val userDoc = db.collection(AuthConst.USERS).document(user.uid).get().await()
        if (!userDoc.exists()) {
            // lần đầu – lưu khung hồ sơ
            val appUser = AppUser(
                uid = user.uid,
                email = user.email ?: email,
                displayName = user.displayName ?: "",
                photoUrl = user.photoUrl?.toString(),
                createdAt = System.currentTimeMillis()
            )
            db.collection(AuthConst.USERS).document(user.uid).set(appUser).await()
            return true to appUser
        }
        val appUser = userDoc.toObject(AppUser::class.java)!!.copy(uid = user.uid)
        return isNew to appUser
    }

    /** Liên kết email/password với user Google hiện tại (để lần sau đăng nhập được bằng pass) */
    suspend fun linkEmailPassword(email: String, password: String) {
        val user = auth.currentUser ?: throw IllegalStateException("No user")
        val cred = EmailAuthProvider.getCredential(email, password)
        user.linkWithCredential(cred).await()
    }

    /** Cập nhật MSSV/phone vào hồ sơ + index MSSV */
    suspend fun completeProfile(
        uid: String,
        mssv: String,
        phone: String,
        institute: String,
        classCode: String
    ) {
        val ref = db.collection(AuthConst.USERS).document(uid)
        ref.update(
            mapOf(
                "mssv" to mssv,
                "phone" to phone,
                "institute" to institute,
                "classCode" to classCode,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }


    /** Đăng nhập bằng MSSV + password: tra email rồi signInWithEmailAndPassword */
    suspend fun signInByMssv(mssv: String, password: String) {
        val snap = db.collection(AuthConst.USERS).whereEqualTo("mssv", mssv).limit(1).get().await()
        if (snap.isEmpty) throw IllegalArgumentException("MSSV không tồn tại")
        val email = snap.documents.first().getString("email") ?: throw IllegalStateException("Email rỗng")
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signInByEmail(email: String, password: String) {
        require(email.endsWith(AuthConst.UTH_DOMAIN)) { "Email phải có đuôi ${AuthConst.UTH_DOMAIN}" }
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun logout() = auth.signOut()
}
