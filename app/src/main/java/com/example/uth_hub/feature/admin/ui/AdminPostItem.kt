package com.example.uth_hub.feature.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uth_hub.app.navigation.Routes
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.post.domain.model.PostModel
import com.example.uth_hub.feature.post.ui.component.PostItem

@Composable
fun AdminPostItem(
    post: PostModel,
    onDeletePost: (String) -> Unit,
    onViewReports: (String) -> Unit,
    onLike: (String) -> Unit = {},
    onComment: (String) -> Unit = {},
    onSave: (String) -> Unit = {},
    navController: NavController? = null,
    isLoading: Boolean = false
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    println("DEBUG AdminPostItem: Post ${post.id} - reportCount = ${post.reportCount}")
    // SỬA: Sử dụng reportCount từ PostModel thay vì hardcode
    val reportCount = post.reportCount

    // Dialog xác nhận xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Xác nhận xóa bài viết",
                    fontWeight = FontWeight.Bold,
                    color = ColorCustom.primaryText
                )
            },
            text = {
                Text(
                    "Bạn có chắc chắn muốn xóa bài viết này? Hành động này không thể hoàn tác.",
                    color = ColorCustom.secondText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePost(post.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Xóa", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy", color = ColorCustom.primary)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .border(
                1.dp,
                color = if (reportCount == 0L) ColorCustom.primary else Color.Red,
                RoundedCornerShape(8.dp)
            )
            .background(
                color = if (reportCount == 0L) ColorCustom.secondBackground else Color(0xFFFFF4F4)
            )
        ,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // PostItem gốc - GIỮ NGUYÊN TẤT CẢ CHỨC NĂNG
        PostItem(
            postModel = post,
            onLike = { onLike(post.id) },
            onComment = {
                onComment(post.id)
                // Sửa chỗ này - đảm bảo route đúng
                navController?.navigate("${Routes.PostComment}/${post.id}")
            },
            onSave = { onSave(post.id) }
        )

        // Thêm admin actions bên dưới
        AdminActionsFooter(
            reportCount = reportCount, // SỬA: Truyền reportCount từ PostModel
            isLoading = isLoading,
            onDeleteClick = { showDeleteDialog = true },
            onViewReportsClick = { onViewReports(post.id) }
        )
    }
}

@Composable
private fun AdminActionsFooter(
    reportCount: Long, // SỬA: Đổi từ Int sang Long để khớp với PostModel
    isLoading: Boolean,
    onDeleteClick: () -> Unit,
    onViewReportsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(end= 10.dp, start = 10.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Report info - SỬA: Hiển thị số lần báo cáo thực tế
        Text(
            text = "Bị tố cáo: $reportCount",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (reportCount > 0) Color.Red else ColorCustom.primary // SỬA: Đổi màu khi có báo cáo
        )

        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Delete button
            Button(
                onClick = onDeleteClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Xóa bài", color = Color.White)
                }
            }
        }
    }
}