package com.example.uth_hub.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.auth.ui.component.AuthCard
import com.example.uth_hub.feature.auth.ui.component.PasswordField
import com.example.uth_hub.feature.auth.ui.component.PrimaryButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.uth_hub.feature.auth.ui.component.AuthBackground

@Composable
fun ResetPasswordScreen(
    onResetDone: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var newPw by remember { mutableStateOf("") }
    var confirmPw by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        AuthBackground()

        // Nút Back
        IconButton(
            onClick = { onBack?.invoke() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 50.dp, start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color.White
            )
        }

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "UTH HUB",
                color = ColorCustom.primarybackground,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 80.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(160.dp))
            Box(Modifier.offset(y = (-20).dp)) {
                AuthCard {
                    Text(
                        text = "ĐỔI MẬT KHẨU",
                        color = ColorCustom.secondText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    // Mật khẩu mới
                    PasswordField(
                        label = "Mật khẩu mới",
                        password = newPw,
                        onValueChange = { newPw = it }
                    )
                    Spacer(Modifier.height(10.dp))

                    // Xác nhận mật khẩu
                    PasswordField(
                        label = "Xác nhận mật khẩu",
                        password = confirmPw,
                        onValueChange = { confirmPw = it }
                    )
                    Spacer(Modifier.height(20.dp))

                    // Nút xác nhận đổi mật khẩu
                    PrimaryButton(
                        text = if (loading) "Đang cập nhật..." else "Xác nhận đổi mật khẩu",
                        enabled = !loading
                    ) {
                        scope.launch {
                            // Ràng buộc logic nhập
                            when {
                                newPw.isBlank() || confirmPw.isBlank() -> {
                                    msg = "⚠️ Vui lòng nhập đủ hai trường mật khẩu"
                                    return@launch
                                }
                                newPw.length < 8 -> {
                                    msg = "⚠️ Mật khẩu phải có ít nhất 8 ký tự"
                                    return@launch
                                }
                                newPw != confirmPw -> {
                                    msg = "⚠️ Mật khẩu xác nhận không khớp"
                                    return@launch
                                }
                            }

                            loading = true
                            try {
                                user?.updatePassword(newPw)?.addOnCompleteListener { task ->
                                    loading = false
                                    if (task.isSuccessful) {
                                        msg = "✅ Đổi mật khẩu thành công!"
                                        onResetDone()
                                    } else {
                                        msg = "❗ Lỗi đổi mật khẩu: ${task.exception?.message}"
                                    }
                                } ?: run {
                                    msg = "❗ Không thể xác thực người dùng."
                                    loading = false
                                }
                            } catch (e: Exception) {
                                msg = "❗ ${e.message}"
                                loading = false
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    msg?.let {
                        Text(
                            text = it,
                            color = when {
                                it.startsWith("✅") -> Color(0xFF00C853)
                                it.startsWith("⚠️") -> Color(0xFFFFAB00)
                                else -> Color(0xFFFF5252)
                            },
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
