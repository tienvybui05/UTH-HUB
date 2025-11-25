package com.example.uth_hub.feature.admin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.uth_hub.R
import com.example.uth_hub.core.design.components.Avartar
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.post.domain.model.PostModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.Bookmark
import compose.icons.fontawesomeicons.regular.Comment
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Heart
import compose.icons.fontawesomeicons.solid.Trash
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminPostItem(
    post: PostModel,
    onDeletePost: (String) -> Unit,
    onViewReports: (String) -> Unit,
    isLoading: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val reportCount by remember(post.id) { mutableStateOf(0) }

    // Dialog xác nhận xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Xác nhận xóa bài viết",
                    fontWeight = FontWeight.Bold,
                    color = ColorCustom.primaryText
                )
            },
            text = {
                Text(
                    text = "Bạn có chắc chắn muốn xóa bài viết này? Hành động này không thể hoàn tác.",
                    color = ColorCustom.secondText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePost(post.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Xóa", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
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
                color = if (reportCount == 0) ColorCustom.primary else Color.Red,
                RoundedCornerShape(8.dp)
            )
            .background(
                color = if (reportCount == 0) ColorCustom.secondBackground else Color(0xFFFFF4F4)
            )
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // Header với thông tin người đăng
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                if (post.authorAvatarUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(post.authorAvatarUrl),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Avartar(R.drawable.avartardefault)
                }

                Column {
                    Text(
                        text = post.authorHandle.ifBlank { "@user" },
                        fontSize = 18.sp,
                        lineHeight = 16.sp,
                        color = ColorCustom.secondText,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = post.authorInstitute.ifBlank { "Chưa cập nhật khoa" },
                        fontSize = 14.sp,
                        lineHeight = 13.sp,
                        color = ColorCustom.primary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.width(250.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatPostDate(post.createdAt),
                            fontSize = 13.sp,
                            lineHeight = 14.sp,
                            color = Color(0xFF595959)
                        )
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Clock,
                            contentDescription = "Ngày Đăng",
                            tint = Color(0xFF595959),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Menu dropdown
            Box {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Menu",
                    tint = ColorCustom.secondText,
                    modifier = Modifier.clickable {
                        expanded = true
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = ColorCustom.primary)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.ExclamationTriangle,
                                    contentDescription = "Xem báo cáo",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("Xem báo cáo", fontSize = 14.sp, color = Color.White)
                            }
                        },
                        onClick = {
                            onViewReports(post.id)
                            expanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.Trash,
                                    contentDescription = "Xóa bài viết",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("Xóa bài viết", fontSize = 14.sp, color = Color.White)
                            }
                        },
                        onClick = {
                            showDeleteDialog = true
                            expanded = false
                        }
                    )
                }
            }
        }

        // Nội dung bài viết
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = post.content,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                color = ColorCustom.secondText,
                maxLines = Int.MAX_VALUE,
                modifier = Modifier.fillMaxWidth()
            )

            // Hiển thị hình ảnh nếu có
            if (post.imageUrls.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(post.imageUrls) { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Ảnh bài đăng",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }

        // Thống kê tương tác
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                // Like
                Row(
                    modifier = Modifier.width(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Heart,
                        contentDescription = "Thích",
                        tint = if (post.likedByMe) Color.Red else ColorCustom.secondText,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = post.likeCount.toString(),
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        color = ColorCustom.secondText
                    )
                }

                // Comment
                Row(
                    modifier = Modifier.width(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Regular.Comment,
                        contentDescription = "Bình luận",
                        tint = ColorCustom.secondText,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = post.commentCount.toString(),
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        color = ColorCustom.secondText
                    )
                }

                // Save
                Row(
                    modifier = Modifier.width(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Regular.Bookmark,
                        contentDescription = "Lưu",
                        tint = if (post.savedByMe) ColorCustom.primary else ColorCustom.secondText,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = post.saveCount.toString(),
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        color = ColorCustom.secondText
                    )
                }
            }
        }

        // Admin actions
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color(0xFF9F9F9F))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lượt bị tố cáo: $reportCount",
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (reportCount > 0) Color.Red else ColorCustom.primaryText
                )
                Button(
                    onClick = { showDeleteDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4646)
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
                        Text(text = "Xóa bài", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Hàm format ngày đăng
@Composable
private fun formatPostDate(timestamp: com.google.firebase.Timestamp?): String {
    return remember(timestamp) {
        if (timestamp != null) {
            val date = timestamp.toDate()
            val now = Date()
            val diff = now.time - date.time

            when {
                diff < 60000 -> "Vừa xong"
                diff < 3600000 -> "${diff / 60000} phút trước"
                diff < 86400000 -> "${diff / 3600000} giờ trước"
                else -> {
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    formatter.format(date)
                }
            }
        } else {
            "Không xác định"
        }
    }
}