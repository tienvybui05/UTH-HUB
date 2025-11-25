package com.example.uth_hub.feature.post.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.example.uth_hub.feature.post.domain.model.PostModel
import androidx.compose.runtime.*
import kotlinx.coroutines.delay



@Composable
fun PostCommentBody(
    modifier: Modifier = Modifier,
    post: PostModel?,
    comments: List<CommentModel>,
    loading: Boolean,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit
) {
    if (loading && post == null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier
    ) {
        PostDetailSection(
            post = post,
            onToggleLike = onToggleLike,
            onToggleSave = onToggleSave
        )

        CommentsListSection(comments = comments)
    }
}

// Card bài viết ở trên
@Composable
fun PostDetailSection(
    post: PostModel?,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit
) {
    post ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        PostItem(
            postModel = post,
            onLike = { onToggleLike() },
            onComment = { /* đang ở trang comment rồi */ },
            onSave = { onToggleSave() }
        )
    }
}

// Danh sách comment
@Composable
fun CommentsListSection(
    comments: List<CommentModel>
) {
    // ⏱ 1 timer duy nhất cho cả list comment
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)                    // mỗi 60 giây
            nowMillis = System.currentTimeMillis()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp)
    ) {
        if (comments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Chưa có bình luận nào")
                }
            }
        } else {
            items(comments, key = { it.id }) { c ->
                CommentItem(
                    comment = c,
                    nowMillis = nowMillis   // 👈 truyền xuống
                )
            }
        }
    }
}
