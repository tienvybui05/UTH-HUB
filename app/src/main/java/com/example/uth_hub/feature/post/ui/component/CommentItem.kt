package com.example.uth_hub.feature.post.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.uth_hub.R
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

@Composable
fun CommentItem(
    comment: CommentModel,
    onOpenProfile: (String) -> Unit,
    onLikeClick: (CommentModel) -> Unit,
    onReplyClick: (CommentModel) -> Unit,
    nowMillis: Long // truyền từ CommentsListSection xuống
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
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = androidx.compose.ui.graphics.Color(0xFFF2F2F2)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = comment.authorName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = ColorCustom.secondText
                    )
                    Spacer(Modifier.height(2.dp))
                    if (comment.text.isNotBlank()) {
                        Text(
                            text = comment.text,
                            fontSize = 14.sp,
                            color = ColorCustom.secondText
                        )
                    }

                    // Media (ảnh / video)
                    if (comment.mediaUrls.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))

                        when (comment.mediaType) {
                            "video" -> {
                                // placeholder đơn giản cho video
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ModeComment,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Video đính kèm",
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            else -> {
                                // mặc định: ảnh
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    comment.mediaUrls.forEach { url ->
                                        AsyncImage(
                                            model = url,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(110.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
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
                    color = androidx.compose.ui.graphics.Color(0xFF999999)
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
