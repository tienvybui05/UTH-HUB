package com.example.uth_hub.feature.notifications.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

// Dữ liệu mô phỏng cho từng thông báo
data class NotificationItem(
    val userName: String,
    val time: String,
    val message: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    // Danh sách thông báo
    val notifications = remember {
        listOf(
            NotificationItem("@tienvybui05", "15 giờ", "Đã thích bài viết của bạn."),
            NotificationItem("@dinhquocdat", "9 giờ", "Đã thích bài viết của bạn."),
            NotificationItem("@tienvybui05", "15 giờ", "Đã bình luận bài viết của bạn."),
            NotificationItem("@LuongITzu", "3 giờ", "Đã thích bài viết của bạn."),
            NotificationItem("@dinhquocdat", "1 giờ", "Đã lưu bài viết của bạn."),
            NotificationItem("@dinhquocdat", "2 giờ", "Đã bình luận bài viết của bạn."),
            NotificationItem("@LuongITzu", "3 giờ", "Đã lưu bài viết của bạn."),
            NotificationItem("@dinhquocdat", "1 giờ", "Đã lưu bài viết của bạn."),
            NotificationItem("@dinhquocdat", "2 giờ", "Đã bình luận bài viết của bạn."),
            NotificationItem("@LuongITzu", "3 giờ", "Đã lưu bài viết của bạn."),
            NotificationItem("@tienvybui05", "15 giờ", "Đã thích bài viết của bạn."),
            NotificationItem("@dinhquocdat", "9 giờ", "Đã thích bài viết của bạn."),
            NotificationItem("@tienvybui05", "15 giờ", "Đã bình luận bài viết của bạn."),
            NotificationItem("@LuongITzu", "3 giờ", "Đã thích bài viết của bạn."),
            NotificationItem("@dinhquocdat", "1 giờ", "Đã lưu bài viết của bạn."),
            NotificationItem("@dinhquocdat", "2 giờ", "Đã bình luận bài viết của bạn."),
        )
    }

    // Giao diện chính
    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 3.dp // hiệu ứng phân tách tự nhiên
            ) {
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
                            text = "Thông báo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF00796B)
                        )
                    }
                )
            }
        }
    ) { padding ->
        // LazyColumn để cuộn danh sách
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(notifications) { item ->
                NotificationRow(item)
            }
        }
    }
}

@Composable
fun NotificationRow(item: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color(0xFF00796B).copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ảnh đại diện
        Image(
            painter = painterResource(id = R.drawable.avartardefault),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Nội dung thông báo
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(item.time, fontSize = 12.sp, color = Color.Gray)
            }
            Text(item.message, fontSize = 14.sp)
        }

        // Nút xóa đỏ
        IconButton(onClick = { /* TODO: xử lý xóa */ }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Xóa",
                tint = Color.Red
            )
        }
    }
}

// Xem thử giao diện trong Android Studio
@Preview(showBackground = true, heightDp = 700)
@Composable
fun PreviewNotificationsScreen() {
    NotificationsScreen(navController = rememberNavController())
}
