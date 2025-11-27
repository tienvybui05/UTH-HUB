package com.example.uth_hub.feature.post.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.example.uth_hub.feature.post.domain.model.PostModel
import kotlinx.coroutines.delay

@Composable
fun PostCommentBody(
    modifier: Modifier = Modifier,
    post: PostModel?,
    comments: List<CommentModel>,
    // id của comment đang được trả lời (để highlight)
    replyingCommentId: String?,
    loading: Boolean,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    onCommentLike: (CommentModel) -> Unit,
    onReplyClick: (CommentModel) -> Unit,
    onOpenProfile: (String) -> Unit,
    // NEW: long-press comment
    onCommentLongClick: (CommentModel) -> Unit
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

        CommentsListSection(
            comments = comments,
            replyingCommentId = replyingCommentId,
            onCommentLike = onCommentLike,
            onReplyClick = onReplyClick,
            onOpenProfile = onOpenProfile,
            onCommentLongClick = onCommentLongClick
        )
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
    comments: List<CommentModel>,
    replyingCommentId: String?,
    onCommentLike: (CommentModel) -> Unit,
    onReplyClick: (CommentModel) -> Unit,
    onOpenProfile: (String) -> Unit,
    onCommentLongClick: (CommentModel) -> Unit
) {
    // 1 timer duy nhất cho cả list comment
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)                    // mỗi 60 giây
            nowMillis = System.currentTimeMillis()
        }
    }

    // group reply theo parent
    val rootComments = remember(comments) {
        comments.filter { it.parentCommentId.isNullOrEmpty() }
    }
    val repliesByParent = remember(comments) {
        comments
            .filter { !it.parentCommentId.isNullOrEmpty() }
            .groupBy { it.parentCommentId!! }
    }

    val highlightColor = Color(0xFFE5F6FB) // màu nền khi comment được chọn để trả lời

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
            items(rootComments, key = { it.id }) { c ->
                // bọc CommentItem trong Box để đổi background khi đang trả lời
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .then(
                            if (replyingCommentId == c.id) {
                                Modifier
                                    .padding(horizontal = 2.dp)
                                    .background(highlightColor)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    CommentItem(
                        comment = c,
                        nowMillis = nowMillis,
                        onOpenProfile = onOpenProfile,
                        onLikeClick = onCommentLike,
                        onReplyClick = onReplyClick,
                        onLongClick = onCommentLongClick
                    )
                }

                // hiển thị các reply (nếu có)
                val replies = repliesByParent[c.id].orEmpty()
                if (replies.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 48.dp)
                    ) {
                        replies.forEach { r ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .then(
                                        if (replyingCommentId == r.id) {
                                            Modifier
                                                .padding(horizontal = 2.dp)
                                                .background(highlightColor)
                                        } else {
                                            Modifier
                                        }
                                    )
                            ) {
                                CommentItem(
                                    comment = r,
                                    nowMillis = nowMillis,
                                    onOpenProfile = onOpenProfile,
                                    onLikeClick = onCommentLike,
                                    onReplyClick = onReplyClick,
                                    replyToAuthorName = c.authorName,
                                    replyToAuthorId = c.authorId,
                                    onLongClick = onCommentLongClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
