package com.example.uth_hub.feature.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    var fullName = mutableStateOf("")
    var phone = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    fun onSignupClick(
        onSendOtp: (email: String) -> Unit
    ) {
        if (fullName.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
            message.value = "Nhập đầy đủ thông tin"
            return
        }
        if (!email.value.endsWith("@uth.edu.vn")) {
            message.value = "Email phải là mail trường (@uth.edu.vn)"
            return
        }
        if (password.value.length < 8) {
            message.value = "Mật khẩu ≥ 8 ký tự"
            return
        }
        if (password.value != confirmPassword.value) {
            message.value = "Xác nhận mật khẩu không khớp"
            return
        }
        message.value = "Đã gửi OTP đến $email"
        onSendOtp(email.value)
    }
}
