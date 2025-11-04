package com.example.uth_hub.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uth_hub.feature.auth.data.AuthRepository
import com.example.uth_hub.feature.auth.ui.component.PasswordField
import com.example.uth_hub.feature.auth.ui.component.PrimaryButton
import com.example.uth_hub.feature.auth.ui.component.UthTextField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun CompleteProfileScreen(
    emailDefault: String,
    onCompleted: () -> Unit
) {
    val repo = remember {
        AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    }
    var mssv by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Hoàn tất hồ sơ")
        Spacer(Modifier.height(8.dp))

        UthTextField(
            label = "Mã số sinh viên",
            value = mssv,
            onValueChange = { mssv = it }
        )
        Spacer(Modifier.height(6.dp))

        UthTextField(
            label = "Số điện thoại",
            value = phone,
            onValueChange = { phone = it }
        )
        Spacer(Modifier.height(6.dp))

        PasswordField(
            label = "Mật khẩu",
            password = password,
            onValueChange = { password = it }
        )
        Spacer(Modifier.height(6.dp))

        PasswordField(
            label = "Xác nhận mật khẩu",
            password = confirm,
            onValueChange = { confirm = it }
        )
        Spacer(Modifier.height(12.dp))

        PrimaryButton(
            text = if (loading) "Đang lưu..." else "Lưu & tiếp tục",
            enabled = !loading
        ) {
            if (mssv.isBlank() || phone.isBlank() || password.length < 8 || password != confirm) {
                msg = "Nhập MSSV/Phone hợp lệ và mật khẩu ≥ 8, khớp xác nhận"
                return@PrimaryButton
            }
            scope.launch {
                try {
                    loading = true
                    repo.completeProfile(uid, mssv, phone)
                    repo.linkEmailPassword(emailDefault, password)
                    onCompleted()
                } catch (e: Exception) {
                    msg = e.message
                } finally {
                    loading = false
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        msg?.let { Text(it) }
    }
}
