package com.example.uth_hub.feature.post.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.rememberAsyncImagePainter
import com.example.uth_hub.R
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

@Composable
fun CommentItem(comment: CommentModel) {
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
                    Text(
                        text = comment.text,
                        fontSize = 14.sp,
                        color = ColorCustom.secondText
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = formatTimeAgo(comment.createdAt),
                fontSize = 12.sp,
                color = androidx.compose.ui.graphics.Color(0xFF999999)
            )
        }
    }
}

private fun formatTimeAgo(timestamp: Timestamp?): String {
    if (timestamp == null) return ""
    val now = System.currentTimeMillis()
    val time = timestamp.toDate().time
    val diff = now - time
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
