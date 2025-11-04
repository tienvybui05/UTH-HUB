package com.example.uth_hub.feature.auth.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.auth.AuthConst
import com.example.uth_hub.feature.auth.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val repo = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    var emailOrMssv = mutableStateOf("")
    var password = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var message = mutableStateOf<String?>(null)

    fun onLoginClick(onSuccess: () -> Unit) = viewModelScope.launch {
        val id = emailOrMssv.value.trim()
        val pass = password.value
        if (id.isBlank() || pass.isBlank()) {
            message.value = "Vui lòng nhập đủ thông tin"
            return@launch
        }
        isLoading.value = true
        try {
            if (id.contains("@")) {
                require(id.endsWith(AuthConst.UTH_DOMAIN)) {
                    "Email phải là mail trường (${AuthConst.UTH_DOMAIN})"
                }
                repo.signInByEmail(id, pass)
            } else {
                repo.signInByMssv(id, pass)
            }
            onSuccess()
        } catch (e: Exception) {
            message.value = e.message
        } finally { isLoading.value = false }
    }

    /** Google client cho Compose screen dùng */
    fun googleClient(context: Context) = repo.buildGoogleClient(context)

    /** Xử lý kết quả Google Sign-In */
    fun handleGoogleResult(
        data: Intent?,
        context: Context,              // 👈 truyền từ UI vào
        onNewUser: () -> Unit,
        onSuccess: () -> Unit
    ) = viewModelScope.launch {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            val email = account.email.orEmpty()
            if (!email.endsWith(AuthConst.UTH_DOMAIN)) {
                // Chặn ngay trên client
                FirebaseAuth.getInstance().signOut()
                repo.buildGoogleClient(context).signOut() // ép chọn lại tài khoản lần sau
                message.value = "Chỉ chấp nhận email ${AuthConst.UTH_DOMAIN}"
                return@launch
            }

            isLoading.value = true
            val (isNew, _) = repo.signInWithGoogle(account)
            if (isNew) onNewUser() else onSuccess()
        } catch (e: Exception) {
            message.value = e.message
        } finally { isLoading.value = false }
    }
}
