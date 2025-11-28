package com.example.uth_hub.feature.post.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.uth_hub.R
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentItem(
    comment: CommentModel,
    nowMillis: Long, // truyền từ CommentsListSection xuống
    onOpenProfile: (String) -> Unit,
    onLikeClick: (CommentModel) -> Unit,
    onReplyClick: (CommentModel) -> Unit,
    // NEW: thông tin người đang được reply (comment cha) – dùng cho reply
    replyToAuthorName: String? = null,
    replyToAuthorId: String? = null,
    // NEW: long-press để mở menu
    onLongClick: (CommentModel) -> Unit,
    // NEW: click media để mở viewer
    onImageClick: (String) -> Unit,
    onVideoClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        val avatarPainter =
            if (comment.authorAvatarUrl.isNotBlank())
                rememberAsyncImagePainter(comment.authorAvatarUrl)
            else
                painterResource(id = R.drawable.avartardefault)

        Image(
            painter = avatarPainter,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { onOpenProfile(comment.authorId) }, // click avatar → mở profile
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF2F2F2),
                modifier = Modifier
                    .combinedClickable(
                        onClick = { /* không làm gì khi click bình thường */ },
                        onLongClick = { onLongClick(comment) }
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Tên tác giả comment
                    Text(
                        text = comment.authorName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = ColorCustom.secondText,
                        modifier = Modifier.clickable {
                            onOpenProfile(comment.authorId) // click tên → mở profile
                        }
                    )
                    Spacer(Modifier.height(2.dp))

                    // Nội dung comment (có xử lý mention của người được reply)
                    if (comment.text.isNotBlank()) {
                        val rawText = comment.text

                        if (!replyToAuthorName.isNullOrBlank() &&
                            rawText.startsWith(replyToAuthorName)
                        ) {
                            val rest = rawText.removePrefix(replyToAuthorName).trimStart()

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Tên người bị reply – click sang profile họ
                                Text(
                                    text = replyToAuthorName,
                                    fontSize = 14.sp,
                                    color = ColorCustom.primary,
                                    modifier = Modifier.clickable {
                                        val targetId = replyToAuthorId ?: comment.authorId
                                        onOpenProfile(targetId)
                                    }
                                )

                                if (rest.isNotBlank()) {
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = rest,
                                        fontSize = 14.sp,
                                        color = ColorCustom.secondText
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = rawText,
                                fontSize = 14.sp,
                                color = ColorCustom.secondText
                            )
                        }
                    }

                    // Media (ảnh / video)
                    if (comment.mediaUrls.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))

                        val firstUrl = comment.mediaUrls.first()

                        when (comment.mediaType) {
                            "video" -> {
                                // 👉 Preview video: card 16:9, có icon play, click mở full-screen có âm thanh
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black)
                                        .clickable { onVideoClick(firstUrl) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Xem video",
                                        tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }

                            else -> {
                                // 👉 Ảnh: fill theo chiều ngang, giữ đúng tỉ lệ 9:16, 4:3… giống Facebook
                                AsyncImage(
                                    model = firstUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onImageClick(firstUrl) },
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // time + actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = formatTimeAgo(comment.createdAt, nowMillis),
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )

                Spacer(Modifier.width(12.dp))

                IconButton(
                    onClick = { onLikeClick(comment) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (comment.likedByMe) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                    )
                }
                if (comment.likeCount > 0) {
                    Text(
                        text = comment.likeCount.toString(),
                        fontSize = 12.sp,
                        color = ColorCustom.secondText
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = { onReplyClick(comment) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = "Reply"
                    )
                }
                Text(
                    text = "Trả lời",
                    fontSize = 12.sp,
                    color = ColorCustom.secondText
                )
            }
        }
    }
}

private fun formatTimeAgo(timestamp: Timestamp?, nowMillis: Long): String {
    if (timestamp == null) return ""

    val time = timestamp.toDate().time
    val diffRaw = nowMillis - time

    // nếu giờ máy bị chậm hơn server → diffRaw âm
    val diff = if (diffRaw < 0) 0L else diffRaw

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)

    return when {
        minutes < 1 -> "Vừa xong"
        minutes < 60 -> "$minutes phút"
        minutes < 60 * 24 -> "${minutes / 60} giờ"
        minutes < 60 * 24 * 7 -> "${minutes / (60 * 24)} ngày"
        minutes < 60 * 24 * 30 -> "${minutes / (60 * 24 * 7)} tuần"
        minutes < 60 * 24 * 365 -> "${minutes / (60 * 24 * 30)} tháng"
        else -> "${minutes / (60 * 24 * 365)} năm"
    }
}
