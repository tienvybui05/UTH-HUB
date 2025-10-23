package com.example.uth_hub.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.R
import com.example.uth_hub.ui.theme.ColorCustom
import androidx.compose.material3.Divider
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePost(navController: NavController) {
    var postContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF00796B)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Tạo bài viết",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF00796B)
                    )
                },
                actions = {
                    TextButton(onClick = {
                        // 👉 Xử lý khi nhấn "Đăng"
                    }) {
                        Text(
                            text = "Đăng",
                            fontSize = 18.sp,
                            color = Color(0xFF00796B),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
            Divider(
                color = Color(0xFF00796B).copy(alpha = 0.4f),
                thickness = 1.dp
            )


        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {
            // ===== Hàng thông tin người dùng =====
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.avartardefault ),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("@tienvybui05",  fontSize = 16.sp)
                    Text(
                        "Viện Công nghệ thông tin và Điện, điện tử",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Hàng icon ảnh / video =====
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { /* mở thư viện ảnh */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Add Image",
                        tint = Color(0xFF00796B),
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = { /* mở camera hoặc video */ }) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Add Camera",
                        tint = Color(0xFF00796B),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Ô nhập nội dung =====
            BasicTextField(
                value = postContent,
                onValueChange = { postContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                decorationBox = { innerTextField ->
                    if (postContent.isEmpty()) {
                        Text(
                            "Hôm nay có gì hot?",
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

// ===== Preview trong Android Studio =====
@Preview(showBackground = true)
@Composable
fun PreviewCreatePost() {
    CreatePost(navController = rememberNavController())
}
