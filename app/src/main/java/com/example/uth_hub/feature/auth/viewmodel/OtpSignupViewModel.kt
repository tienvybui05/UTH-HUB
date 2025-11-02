package com.example.uth_hub.feature.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class OtpSignupViewModel : ViewModel() {
    var otp = mutableStateOf("")
    var message = mutableStateOf<String?>(null)

    fun onVerifyClick(
        onVerified: () -> Unit
    ) {
        if (otp.value.length < 4) {
            message.value = "OTP phải đủ 4 số"
            return
        }
        message.value = "Xác thực thành công"
        onVerified()
    }
}
