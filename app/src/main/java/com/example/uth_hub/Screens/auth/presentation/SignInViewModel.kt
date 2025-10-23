package com.example.uth_hub.Screens.auth.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var message = mutableStateOf<String?>(null)

    fun onLoginClick(
        onSuccess: () -> Unit
    ) {
        if (email.value.isBlank() || password.value.isBlank()) {
            message.value = "Vui lòng nhập đủ email & mật khẩu"
            return
        }
        if (!email.value.endsWith("@ut.edu.vn")) {
            message.value = "Email phải là mail trường (@uth.edu.vn)"
            return
        }
        isLoading.value = true
        // giả lập thành công
        isLoading.value = false
        message.value = "Đăng nhập thành công"
        onSuccess()
    }
}
