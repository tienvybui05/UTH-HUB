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
    // long-press comment để mở menu
    onCommentLongClick: (CommentModel) -> Unit,
    // click media trong comment
    onImageClick: (String) -> Unit,
    onVideoClick: (String) -> Unit
) {
    if (loading && post == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // ====== PHẦN TIME AGO DÙNG CHUNG ======
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)               // mỗi 60 giây cập nhật lại label thời gian
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

    // ✅ TOÀN BỘ MÀN HÌNH LÀ 1 LAZYCOLUMN:
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp)
    ) {
        // ----- ITEM: BÀI VIẾT Ở ĐẦU -----
        item(key = "post") {
            PostDetailSection(
                post = post,
                onToggleLike = onToggleLike,
                onToggleSave = onToggleSave
            )
        }

        // ----- ITEM: "CHƯA CÓ BÌNH LUẬN" -----
        if (comments.isEmpty()) {
            item(key = "empty_comments") {
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
            // ----- ITEM: CÁC COMMENT GỐC + REPLY -----
            items(rootComments, key = { it.id }) { c ->
                // Comment gốc
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
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
                        onLongClick = onCommentLongClick,
                        onImageClick = onImageClick,
                        onVideoClick = onVideoClick
                    )
                }

                // Reply của comment đó (nếu có)
                val replies = repliesByParent[c.id].orEmpty()
                if (replies.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 48.dp, end = 16.dp)
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
                                    onLongClick = onCommentLongClick,
                                    onImageClick = onImageClick,
                                    onVideoClick = onVideoClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Card bài viết ở trên (giữ nguyên logic)
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
