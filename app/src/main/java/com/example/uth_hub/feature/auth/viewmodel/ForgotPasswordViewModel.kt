package com.example.uth_hub.feature.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ForgotPasswordViewModel : ViewModel() {
    var email = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    fun onSendOtpClick(
        onSent: (email: String) -> Unit
    ) {
        if (!email.value.endsWith("@ut.edu.vn")) {
            message.value = "Nhập email @ut.edu.vn"
            return
        }
        message.value = "Đã gửi OTP đến ${email.value}"
        onSent(email.value)
    }
}
