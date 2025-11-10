@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.uth_hub.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.auth.data.AuthRepository
import com.example.uth_hub.feature.auth.ui.component.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Danh sách 7 viện
private val INSTITUTES = listOf(
    "Viện CNTT & Điện, điện tử",
    "Viện Cơ khí",
    "Viện Đường sắt tốc độ cao",
    "Viện Kinh tế & Phát triển Giao thông Vận tải",
    "Viện Hàng hải",
    "Viện Ngôn ngữ, Khoa học Chính trị & Xã hội",
    "Viện Nghiên cứu & Đào tạo Đèo Cả"
)

@Composable
fun CompleteProfileScreen(
    emailDefault: String,
    onCompleted: () -> Unit
) {
    val inPreview = LocalInspectionMode.current

    val repo = remember(inPreview) {
        if (inPreview) null
        else AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    }
    val uid = remember(inPreview) {
        if (inPreview) "preview-uid" else FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    }

    var mssv by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var classCode by remember { mutableStateOf("") }
    var institute by remember { mutableStateOf("") }
    var instituteExpanded by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    var msg by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        AuthBackground()

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
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(160.dp))
            Box(Modifier.offset(y = (-20).dp)) {
                AuthCard {
                    Text(
                        text = "Hoàn tất hồ sơ",
                        color = ColorCustom.secondText,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // MSSV
                    UthTextField(
                        label = "Mã số sinh viên",
                        value = mssv,
                        onValueChange = { mssv = it }
                    )
                    Spacer(Modifier.height(6.dp))

                    // SĐT
                    UthTextField(
                        label = "Số điện thoại",
                        value = phone,
                        onValueChange = { phone = it }
                    )
                    Spacer(Modifier.height(6.dp))

                    // Viện (Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = instituteExpanded,
                        onExpandedChange = { instituteExpanded = !instituteExpanded }
                    ) {
                        TextField(
                            value = institute,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Viện") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = instituteExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = instituteExpanded,
                            onDismissRequest = { instituteExpanded = false }
                        ) {
                            INSTITUTES.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        institute = item
                                        instituteExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))

                    // Lớp
                    UthTextField(
                        label = "Lớp",
                        value = classCode,
                        onValueChange = { classCode = it }
                    )
                    Spacer(Modifier.height(6.dp))

                    // Mật khẩu
                    PasswordField(
                        label = "Mật khẩu",
                        password = password,
                        onValueChange = { password = it }
                    )
                    Spacer(Modifier.height(6.dp))

                    // Xác nhận mật khẩu
                    PasswordField(
                        label = "Xác nhận mật khẩu",
                        password = confirm,
                        onValueChange = { confirm = it }
                    )
                    Spacer(Modifier.height(12.dp))

                    // Nút Lưu & Tiếp tục
                    PrimaryButton(
                        text = if (loading) "Đang lưu..." else "Lưu & tiếp tục",
                        enabled = !loading
                    ) {
                        // Clear message trước
                        msg = null

                        // Validate cơ bản - CHẠY TRÊN UI THREAD
                        if (mssv.isBlank() || phone.isBlank() || institute.isBlank() || classCode.isBlank()) {
                            msg = "❌ Vui lòng nhập đủ MSSV, SĐT, Viện và Lớp"
                            return@PrimaryButton
                        }
                        if (password.length < 8) {
                            msg = "❌ Mật khẩu phải có ít nhất 8 ký tự"
                            return@PrimaryButton
                        }
                        if (password != confirm) {
                            msg = "❌ Mật khẩu xác nhận không khớp"
                            return@PrimaryButton
                        }

                        if (inPreview) {
                            msg = "✅ (Preview) Giả lập lưu hồ sơ"
                            onCompleted()
                            return@PrimaryButton
                        }

                        scope.launch {
                            loading = true

                            // ĐẢM BẢO UI UPDATE CHẠY TRÊN MAIN THREAD
                            msg = "⏳ Đang lưu dữ liệu..."

                            try {
                                // 1) Lưu hồ sơ
                                msg = "📝 Đang lưu thông tin hồ sơ..."
                                repo?.completeProfile(uid, mssv, phone, institute, classCode)

                                // 2) Thử link email/password (bỏ qua lỗi)
                                msg = "🔗 Đang liên kết tài khoản..."
                                try {
                                    repo?.linkEmailPassword(emailDefault, password)
                                } catch (e: Exception) {
                                    // Bỏ qua lỗi link
                                }

                                // 3) Thử reload Auth (bỏ qua lỗi)
                                try {
                                    FirebaseAuth.getInstance().currentUser?.reload()
                                } catch (_: Exception) {}

                                // 4) Thành công
                                msg = "✅ Hoàn tất hồ sơ thành công! Đang chuyển trang..."
                                delay(1000) // Cho user đọc thông báo

                                onCompleted()

                            } catch (e: Exception) {
                                // Hiển thị lỗi cụ thể
                                msg = "❌ Lỗi: ${e.message ?: "Không xác định"}"
                            } finally {
                                loading = false
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Hiển thị thông báo - ĐƠN GIẢN HÓA
                    if (msg != null) {
                        Text(
                            text = msg!!,
                            color = when {
                                msg!!.contains("✅") -> Color(0xFF388E3C) // Green
                                msg!!.contains("❌") -> Color(0xFFD32F2F) // Red
                                msg!!.contains("⏳") -> Color(0xFF757575) // Gray
                                else -> Color(0xFF1976D2) // Blue
                            },
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompleteProfilePreview() {
    MaterialTheme {
        Surface {
            CompleteProfileScreen(
                emailDefault = "student@ut.edu.vn",
                onCompleted = {}
            )
        }
    }
}