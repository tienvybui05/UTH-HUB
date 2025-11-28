package com.example.uth_hub.feature.auth.data

import android.content.Context
import com.example.uth_hub.app.navigation.UserRole
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
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    /** Tạo GoogleSignInClient */
    fun buildGoogleClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                context.getString(
                    // lấy web client id từ google-services.json (default_web_client_id)
                    com.example.uth_hub.R.string.default_web_client_id
                )
            )
            .requestEmail()
            .setHostedDomain("ut.edu.vn") // <-- chỉ là hint, có thể bị bỏ qua
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    //  🔥 Thêm hàm cập nhật FCM Token (CHỖ SỬA 1)
    // ===============================================================
    private suspend fun updateFcmToken(uid: String) {
        val token = com.google.firebase.messaging.FirebaseMessaging
            .getInstance()
            .token
            .await()

        db.collection(AuthConst.USERS)
            .document(uid)
            .update("fcmToken", token)
            .await()
    }
    // ===============================================================


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
                role = UserRole.STUDENT,
                fcmToken = null,
                createdAt = System.currentTimeMillis()
            )
            db.collection(AuthConst.USERS).document(user.uid).set(appUser).await()
            // 🔥 Cập nhật token ngay sau khi tạo user mới
            updateFcmToken(user.uid)
            return true to appUser
        }
        val appUser = userDoc.toObject(AppUser::class.java)!!.copy(uid = user.uid)
        //  🔥 CHỖ SỬA 3 — luôn update token khi user đăng nhập Google
        // ===============================================================
        updateFcmToken(user.uid)

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
        // 🔥 Bọc truy vấn Firestore để bắt PERMISSION_DENIED và convert thành lỗi "đẹp"
        val snap = try {
            db.collection(AuthConst.USERS)
                .whereEqualTo("mssv", mssv)
                .limit(1)
                .get()
                .await()
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                // Lỗi này sẽ được ViewModel hiện dưới ô MSSV (IllegalArgumentException)
                throw IllegalArgumentException(
                    "Không thể tra cứu MSSV. Kiểm tra lại quyền đọc collection users trong Firestore rules."
                )
            } else {
                throw e
            }
        }

        if (snap.isEmpty) throw IllegalArgumentException("MSSV không tồn tại")

        val email = snap.documents.first().getString("email")
            ?: throw IllegalArgumentException("Tài khoản này chưa có email trong hồ sơ")

        auth.signInWithEmailAndPassword(email, password).await()
        //  🔥 CHỖ SỬA 4 — update FCM token sau khi login MSSV
        // ===============================================================
        updateFcmToken(auth.currentUser!!.uid)
    }

    suspend fun signInByEmail(email: String, password: String) {
        require(email.endsWith(AuthConst.UTH_DOMAIN)) { "Email phải có đuôi ${AuthConst.UTH_DOMAIN}" }
        auth.signInWithEmailAndPassword(email, password).await()
        //  🔥 CHỖ SỬA 5 — update FCM token sau khi login email
        // ===============================================================
        updateFcmToken(auth.currentUser!!.uid)
    }

    fun logout() = auth.signOut()
}
