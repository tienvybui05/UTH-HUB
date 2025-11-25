package com.example.uth_hub.feature.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AboutTermsScreen(navController: NavController) {

    val brandColor = Color(0xFF176569)

    Column(Modifier.fillMaxSize()) {

        /* ============================
           TOP BAR GỌN, NỀN TRẮNG
        ============================ */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),     // 👈 giảm chiều cao
            contentAlignment = Alignment.Center
        ) {

            // Nút back
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = brandColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Title
            Text(
                text = "Giới thiệu & Điều khoản",
                color = brandColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }

        /* ============================
           NỘI DUNG
        ============================ */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(12.dp))

            /* === GIỚI THIỆU === */
            Text(
                text = "Giới thiệu",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = brandColor
            )
            Spacer(Modifier.height(6.dp))

            Text(
                text = """
UTH Hub là nền tảng mạng xã hội thu nhỏ và nội bộ chỉ dành cho sinh viên Trường Đại học Giao thông Vận tải TP.HCM
Với phong cách khép kín, riêng tư, đơn giản và thân thiện, ứng dụng giúp:
- Kết nối sinh viên trong cùng trường, cùng khoa để trao đổi học thuật và chia sẻ kinh nghiệm  
- Cập nhật các thông tin sự kiện, hoạt động, và phong trào trong trường một cách nhanh chóng  
- Giải trí & giao lưu trong một môi trường an toàn, tránh nội dung độc hại hoặc spam  
                """.trimIndent(),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(22.dp))

            /* === ĐIỀU KHOẢN === */
            Text(
                text = "Điều khoản sử dụng",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = brandColor
            )
            Spacer(Modifier.height(6.dp))

            Text(
                text = """
• Người dùng phải tuân thủ pháp luật Việt Nam và quy định của Nhà trường.
• Không đăng tải nội dung sai sự thật, phản cảm hoặc xúc phạm, quấy rối cá nhân/tổ chức.
• Không chia sẻ tài liệu có bản quyền khi chưa được phép.
• Không sử dụng nền tảng cho mục đích thương mại hoặc spam.
• Hệ thống có quyền tạm khóa tài khoản nếu phát hiện vi phạm.
                """.trimIndent(),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(22.dp))

            /* === CHÍNH SÁCH QUYỀN RIÊNG TƯ === */
            Text(
                text = "Chính sách quyền riêng tư",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = brandColor
            )
            Spacer(Modifier.height(6.dp))

            Text(
                text = """
UTH Hub cam kết bảo vệ dữ liệu cá nhân theo các nguyên tắc:

• Không bán hoặc chia sẻ dữ liệu cho bên thứ ba.
• Ảnh đại diện, thông tin hồ sơ & bài viết chỉ phục vụ tính năng hiển thị trong ứng dụng.
• Người dùng có quyền chỉnh sửa hoặc xóa dữ liệu cá nhân bất kỳ lúc nào.
• Dữ liệu chỉ được cung cấp cho cơ quan pháp luật khi có yêu cầu hợp lệ.

Hệ thống sử dụng các tiêu chuẩn bảo mật hiện đại để bảo vệ thông tin người dùng.
                """.trimIndent(),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}
