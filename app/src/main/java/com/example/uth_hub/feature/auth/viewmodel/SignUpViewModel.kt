package com.example.uth_hub.feature.auth.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.auth.AuthConst
import com.example.uth_hub.feature.auth.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val repo = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    val isLoading = mutableStateOf(false)
    val message = mutableStateOf<String?>(null)

    /** Client đăng nhập Google để UI dùng */
    fun googleClient(context: Context): GoogleSignInClient = repo.buildGoogleClient(context)

    /** Xử lý kết quả Google Sign-In cho flow ĐĂNG KÝ */
    fun handleGoogleResult(
        data: Intent?,
        context: Context,
        onNewUser: (emailFromGoogle: String) -> Unit, // -> sang CompleteProfile
        onAlreadyHasAccount: () -> Unit               // -> quay về SignIn
    ) = viewModelScope.launch {
        message.value = null
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)

            val email = account.email.orEmpty()
            if (!email.endsWith(AuthConst.UTH_DOMAIN)) {
                // chặn email ngoài trường
                FirebaseAuth.getInstance().signOut()
                // chỉ cần signOut client là đủ, KHÔNG cần asGoogleSignInOptions
                googleClient(context).signOut()
                message.value = "Chỉ chấp nhận email ${AuthConst.UTH_DOMAIN}"
                return@launch
            }

            isLoading.value = true
            val (isNew, _) = repo.signInWithGoogle(account)
            if (isNew) {
                onNewUser(email)
            } else {
                message.value = "Tài khoản đã tồn tại. Hãy đăng nhập."
                onAlreadyHasAccount()
            }
        } catch (e: Exception) {
            message.value = e.message ?: "Đăng ký thất bại, thử lại sau."
        } finally {
            isLoading.value = false
        }
    }
}
