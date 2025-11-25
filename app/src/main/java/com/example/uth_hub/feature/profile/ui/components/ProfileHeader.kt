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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.uth_hub.R
import com.example.uth_hub.core.design.theme.ColorCustom

@Composable
fun ProfileHeader(
    name: String?,
    username: String?,           // MSSV
    major: String?,
    code: String?,
    isOwner: Boolean,            //  true = profile mình, false = profile người khác
    onShareClick: () -> Unit,    // luôn dùng
    avatarUrl: String? = null,   // url ảnh đại diện
    onEditClick: () -> Unit = {},// chỉ dùng khi isOwner = true
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
    ) {
        // Ảnh nền
        Image(
            painter = painterResource(id = R.drawable.nenprofile),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        // Overlay gradient (trong suốt -> đậm dần)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x00000000), Color(0x66000000))
                    )
                )
        )

        // Nội dung
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.Center)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = name.orEmpty().ifBlank { "—" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = username
                            ?.let { if (it.all(Char::isDigit)) it else "@$it" }
                            .orEmpty()
                            .ifBlank { "—" },
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = major.orEmpty().ifBlank { "—" },
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Lớp: ${code.orEmpty().ifBlank { "—" }}",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Avatar (URL -> Coil, fallback ảnh mặc định)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.avartardefault),
                    error = painterResource(R.drawable.avartardefault),
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            //Phân nhánh: nếu là owner → 2 nút; nếu là người khác → chỉ 1 nút Chia sẻ
            if (isOwner) {
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
                            fontWeight = FontWeight.Medium
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
            } else {
                OutlinedButton(
                    onClick = onShareClick,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
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

//@Preview(showBackground = true)
//@Composable
//fun PreviewProfileHeader() {
//    Surface(color = ColorCustom.primaryText) {
//        ProfileHeader(
//            name = "Đạt Vỹ Lượng",
//            username = "051205011574",
//            major = "Viện CNTT & Điện, điện tử",
//            code = "CN2301C",
//            isOwner = true,
//            onShareClick = {},
//            avatarUrl = null,
//            onEditClick = {}
//        )
//    }
//}
