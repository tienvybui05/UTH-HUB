package com.example.uth_hub.feature.post.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.uth_hub.R
import com.example.uth_hub.core.design.components.PostItem
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.example.uth_hub.feature.post.viewmodel.CommentsViewModel
import com.example.uth_hub.feature.post.viewmodel.CommentsViewModelFactory
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCommentScreen(
    navController: NavController,
    postId: String
) {
    val vm: CommentsViewModel = viewModel(
        factory = CommentsViewModelFactory(
            postId = postId,
            repo = PostDI.providePostRepository(),
            auth = PostDI.auth
        )
    )

    val post by vm.post.collectAsState()
    val comments by vm.comments.collectAsState()
    val commentText by vm.commentText.collectAsState()
    val loading by vm.loading.collectAsState()
    val sending by vm.sending.collectAsState()

    Scaffold(
        topBar = {
            Surface(color = Color.White, shadowElevation = 3.dp) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ColorCustom.primary
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Bài viết của ${post?.authorName ?: ""}",
                            fontSize = 18.sp,
                            color = ColorCustom.primary
                        )
                    }
                )
            }
        },
        bottomBar = {
            CommentInputBar(
                value = commentText,
                onValueChange = vm::onCommentTextChange,
                onSend = vm::sendComment,
                sending = sending
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            if (loading && post == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            // Card bài viết ở trên
            post?.let { p ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    PostItem(
                        postModel = p,
                        onLike = { vm.toggleLike() },
                        onComment = { /* đang ở trang comment rồi */ },
                        onSave = { vm.toggleSave() }
                    )
                }
            }

            // Danh sách comment
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp)
            ) {
                items(comments, key = { it.id }) { c ->
                    CommentItem(comment = c)
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: CommentModel) {
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
            // Bubble xám chứa cả tên + nội dung
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF2F2F2)
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
                color = Color(0xFF999999)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    sending: Boolean
) {
    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Gửi bình luận",
                    tint = ColorCustom.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
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
