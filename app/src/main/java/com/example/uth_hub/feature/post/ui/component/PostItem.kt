package com.example.uth_hub.feature.post.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PostItem(
    postModel: PostModel,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onSave: () -> Unit
) {
    var expanded = remember { mutableStateOf(false) }

    val avatarPainter =
        if (postModel.authorAvatarUrl.isNotBlank())
            rememberAsyncImagePainter(model = postModel.authorAvatarUrl)
        else
            painterResource(id = R.drawable.avartardefault)

    val dateText =
        postModel.createdAt?.toDate()?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
        } ?: ""

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
                    Text(
                        text = if (postModel.authorHandle.isNotBlank()) postModel.authorHandle else "@unknown",
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateText,
                            fontSize = 13.sp,
                            lineHeight = 14.sp,
                            color = Color(0xFF595959)
                        )
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Clock,
                            contentDescription = "Ngày đăng",
                            tint = Color(0xFF595959),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

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
                        onClick = { expanded.value = false }
                    )
                }
            }
        }

        // Nội dung
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(postModel.imageUrls) { url ->
                        Image(
                            painter = rememberAsyncImagePainter(model = url),
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
    }
}
