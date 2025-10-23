package com.example.uth_hub.Screens.auth.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ResetPasswordViewModel : ViewModel() {
    var newPassword = mutableStateOf("")
    var confirmPassword = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    fun onResetClick(
        onResetDone: () -> Unit
    ) {
        if (newPassword.value.length < 8) {
            message.value = "Mật khẩu ≥ 8 ký tự"
            return
        }
        if (newPassword.value != confirmPassword.value) {
            message.value = "Xác nhận mật khẩu không khớp"
            return
        }
        message.value = "Đổi mật khẩu thành công"
        onResetDone()
    }
}
