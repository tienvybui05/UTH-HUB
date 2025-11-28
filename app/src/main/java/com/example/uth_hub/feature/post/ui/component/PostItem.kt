package com.example.uth_hub.feature.post.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.uth_hub.R
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
import kotlinx.coroutines.delay

@Composable
fun PostItem(
    postModel: PostModel,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onSave: () -> Unit,
    // callback business: chỉ được gọi sau khi user bấm "Có" trong dialog báo cáo
    onReport: () -> Unit = {},
    // click ảnh bài viết để mở full-screen
    onImageClick: (String) -> Unit = {}
) {
    val expanded = remember { mutableStateOf(false) }
    val showReportDialog = remember { mutableStateOf(false) }

    val avatarPainter =
        if (postModel.authorAvatarUrl.isNotBlank())
            rememberAsyncImagePainter(model = postModel.authorAvatarUrl)
        else
            painterResource(id = R.drawable.avartardefault)

    //  Time-ago auto cập nhật mỗi 60s
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            nowMillis = System.currentTimeMillis()
        }
    }
    val timeAgo = formatTimeAgo(postModel.createdAt, nowMillis)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .border(1.dp, ColorCustom.primary, RoundedCornerShape(8.dp))
            .background(color = ColorCustom.secondBackground)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // Header: avatar + info + menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = avatarPainter,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column {
                    // 🔹 Dùng authorName, fallback về handle rồi @unknown
                    Text(
                        text = postModel.authorName
                            .ifBlank { postModel.authorHandle.ifBlank { "@unknown" } },
                        fontSize = 18.sp,
                        lineHeight = 16.sp,
                        color = ColorCustom.secondText
                    )
                    Text(
                        text = postModel.authorInstitute,
                        fontSize = 14.sp,
                        lineHeight = 13.sp,
                        color = Color(0xFF595959),
                        maxLines = Int.MAX_VALUE,
                        modifier = Modifier.width(250.dp)
                    )
                    if (timeAgo.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = timeAgo,
                                fontSize = 13.sp,
                                lineHeight = 14.sp,
                                color = Color(0xFF595959)
                            )
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Clock,
                                contentDescription = "Thời gian đăng",
                                tint = Color(0xFF595959),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            // Menu 3 chấm: Báo cáo bài viết vi phạm
            Box {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Menu",
                    tint = ColorCustom.secondText,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { expanded.value = true }
                )
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = ColorCustom.primary)
                        .padding(end = 10.dp, start = 10.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color.Transparent),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.ExclamationTriangle,
                                    contentDescription = "Tố cáo bài viết vi phạm",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    "Báo cáo bài viết vi phạm",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        },
                        modifier = Modifier.background(color = ColorCustom.primary),
                        onClick = {
                            expanded.value = false
                            showReportDialog.value = true
                        }
                    )
                }
            }
        }

        // Nội dung text + ảnh
        Column(modifier = Modifier.fillMaxWidth()) {
            if (postModel.content.isNotBlank()) {
                Text(
                    text = postModel.content,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    color = ColorCustom.secondText,
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (postModel.imageUrls.isNotEmpty()) {
                val imageUrls = postModel.imageUrls.filter { it.isNotBlank() }
                val spacing = 8.dp

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    var i = 0
                    while (i < imageUrls.size) {
                        val remaining = imageUrls.size - i

                        when {
                            // 🔹 1 ảnh: fill ngang, KHÔNG ép aspectRatio -> giữ đúng tỉ lệ gốc
                            remaining == 1 -> {
                                val url = imageUrls[i]
                                Image(
                                    painter = rememberAsyncImagePainter(model = url),
                                    contentDescription = "Ảnh bài đăng",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.LightGray)
                                        .clickable { onImageClick(url) }
                                )
                                i++
                            }

                            // 🔹 >= 2 ảnh: dạng grid 2 ảnh vuông 1 hàng
                            else -> {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    val end = minOf(i + 2, imageUrls.size)
                                    imageUrls.subList(i, end).forEachIndexed { index, url ->
                                        Image(
                                            painter = rememberAsyncImagePainter(model = url),
                                            contentDescription = "Ảnh bài đăng",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.LightGray)
                                                .clickable { onImageClick(url) }
                                        )
                                        if (index == 0 && end - i == 2) {
                                            Spacer(modifier = Modifier.width(spacing))
                                        }
                                    }
                                }
                                i += 2
                            }
                        }
                    }
                }
            }
        }

        // Actions: Like - Comment - Save
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                // Like
                IconButton(
                    onClick = onLike,
                    modifier = Modifier.width(80.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Heart,
                            contentDescription = "Thích",
                            tint = if (postModel.likedByMe) Color.Red else ColorCustom.secondText,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${postModel.likeCount}",
                            fontSize = 16.sp,
                            lineHeight = 18.sp,
                            color = ColorCustom.secondText
                        )
                    }
                }

                // Comment
                IconButton(
                    onClick = onComment,
                    modifier = Modifier.width(80.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Regular.Comment,
                            contentDescription = "Bình luận",
                            tint = ColorCustom.secondText,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${postModel.commentCount}",
                            fontSize = 16.sp,
                            color = ColorCustom.secondText
                        )
                    }
                }

                // Save
                IconButton(
                    onClick = onSave,
                    modifier = Modifier.width(80.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Regular.Bookmark,
                            contentDescription = "Lưu",
                            tint = if (postModel.savedByMe) ColorCustom.primary else ColorCustom.secondText,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${postModel.saveCount}",
                            fontSize = 16.sp,
                            lineHeight = 18.sp,
                            color = ColorCustom.secondText
                        )
                    }
                }
            }

            Row { /* chừa chỗ cho các action khác nếu cần */ }
        }

        // Dialog xác nhận báo cáo
        if (showReportDialog.value) {
            AlertDialog(
                onDismissRequest = { showReportDialog.value = false },
                title = { Text("Báo cáo bài viết") },
                text = { Text("Bạn muốn báo cáo bài viết này vi phạm?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showReportDialog.value = false
                            onReport()
                        }
                    ) {
                        Text("Có")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReportDialog.value = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}
