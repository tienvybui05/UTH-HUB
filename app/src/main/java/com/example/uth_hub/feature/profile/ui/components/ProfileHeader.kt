package com.example.uth_hub.feature.profile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.R
import com.example.uth_hub.core.design.theme.ColorCustom

@Composable
fun ProfileHeader(
    name: String,
    username: String,
    major: String,
    code: String,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp) // 🔹 Chiều cao cố định
    ) {
        // --- Ảnh nền căn giữa ---
        Image(
            painter = painterResource(id = R.drawable.nenprofile),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter), // ✅ căn theo mép trên
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter // ✅ hiển thị phần phía trên của ảnh
        )

        // --- Overlay mờ đen để nổi chữ ---
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0x40000000))
        )

        // --- Nội dung header ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.Center),
        ) {
            // Hàng thông tin + avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "@$username",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = major,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Lớp: $code",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                // Avatar góc phải
                Image(
                    painter = painterResource(id = R.drawable.avartardefault),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- Hai nút hành động ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(
                        "Chỉnh sửa trang cá nhân",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                OutlinedButton(
                    onClick = onShareClick,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(
                        "Chia sẻ trang cá nhân",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileHeader() {
    Surface(color = ColorCustom.primaryText) {
        ProfileHeader(
            name = "Đạt Vỹ Lượng",
            username = "anhdeptraio4",
            major = "Viện CNTT & Điện, điện tử",
            code = "CN2301C",
            onEditClick = {},
            onShareClick = {}
        )
    }
}
