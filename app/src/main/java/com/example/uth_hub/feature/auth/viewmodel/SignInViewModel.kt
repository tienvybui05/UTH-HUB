package com.example.uth_hub.feature.auth.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.auth.AuthConst
import com.example.uth_hub.feature.auth.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val repo = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    var emailOrMssv = mutableStateOf("")
    var password = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    // 👇 lỗi theo từng ô + lỗi chung
    var idError = mutableStateOf<String?>(null)      // cho ô MSSV/Email
    var passError = mutableStateOf<String?>(null)    // cho ô Password
    var message = mutableStateOf<String?>(null)      // lỗi chung (toast/ dưới nút)

    fun onLoginClick(onSuccess: () -> Unit) = viewModelScope.launch {
        // reset lỗi cũ
        idError.value = null
        passError.value = null
        message.value = null

        val id = emailOrMssv.value.trim()
        val pass = password.value

        // validate rỗng
        var hasErr = false
        if (id.isBlank()) { idError.value = "Tài khoản đăng nhập là bắt buộc"; hasErr = true }
        if (pass.isBlank()) { passError.value = "Mật khẩu là bắt buộc"; hasErr = true }
        if (hasErr) return@launch

        isLoading.value = true
        try {
            if (id.contains("@")) {
                // nhập email
                if (!id.endsWith(AuthConst.UTH_DOMAIN)) {
                    idError.value = "Chỉ chấp nhận email ${AuthConst.UTH_DOMAIN}"
                    return@launch
                }
                repo.signInByEmail(id, pass)
            } else {
                // nhập MSSV
                repo.signInByMssv(id, pass)
            }
            onSuccess()
        } catch (e: Exception) {
            when (e) {
                // sai mật khẩu
                is FirebaseAuthInvalidCredentialsException -> {
                    passError.value = "Mật khẩu không đúng"
                }
                // user không tồn tại / email chưa đăng ký
                is FirebaseAuthInvalidUserException -> {
                    idError.value = "Tài khoản không tồn tại"
                }
                // từ Repository: MSSV không tồn tại, domain sai, v.v.
                is IllegalArgumentException -> {
                    idError.value = e.message
                }
                else -> {
                    message.value = e.message ?: "Đăng nhập thất bại, thử lại sau"
                }
            }
        } finally {
            isLoading.value = false
        }
    }

    fun googleClient(context: Context) = repo.buildGoogleClient(context)

    fun handleGoogleResult(
        data: Intent?,
        context: Context,
        onNewUser: () -> Unit,
        onSuccess: () -> Unit
    ) = viewModelScope.launch {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val email = account.email.orEmpty()
            if (!email.endsWith(AuthConst.UTH_DOMAIN)) {
                FirebaseAuth.getInstance().signOut()
                repo.buildGoogleClient(context).signOut()
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
