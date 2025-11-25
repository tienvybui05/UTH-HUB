package com.example.uth_hub.feature.post.ui.component

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.uth_hub.core.design.theme.ColorCustom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    sending: Boolean,
    mediaUris: List<Uri>,
    mediaType: String?,
    onPickImages: () -> Unit,
    onPickVideo: () -> Unit,
    onClearMedia: () -> Unit
) {
    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            //  Preview media nếu có
            if (mediaUris.isNotEmpty()) {
                SelectedMediaPreviewRow(
                    mediaUris = mediaUris,
                    mediaType = mediaType,
                    onClearMedia = onClearMedia
                )
                Spacer(Modifier.height(6.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chọn ảnh
                IconButton(onClick = onPickImages) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Chọn ảnh"
                    )
                }

                // Chọn video
                IconButton(onClick = onPickVideo) {
                    Icon(
                        imageVector = Icons.Outlined.Videocam,
                        contentDescription = "Chọn video"
                    )
                }

                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Viết bình luận...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank() && !sending
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Gửi bình luận",
                        tint = ColorCustom.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedMediaPreviewRow(
    mediaUris: List<Uri>,
    mediaType: String?,
    onClearMedia: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (mediaType == "video") {
            // Video: chỉ hiển thị 1 card mô tả
            Surface(
                shape = RoundedCornerShape(10.dp),
                tonalElevation = 1.dp,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Videocam,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "1 video đã chọn",
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            // Ảnh: hiển thị dạng list ngang
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(mediaUris) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        IconButton(onClick = onClearMedia) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Xoá media"
            )
        }
    }
}
