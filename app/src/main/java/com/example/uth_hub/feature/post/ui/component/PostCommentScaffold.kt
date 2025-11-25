package com.example.uth_hub.feature.post.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.example.uth_hub.feature.post.domain.model.PostModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCommentScaffold(
    authorName: String,
    post: PostModel?,
    comments: List<CommentModel>,
    commentText: String,
    loading: Boolean,
    sending: Boolean,
    onBack: () -> Unit,
    onCommentTextChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit
) {
    Scaffold(
        topBar = {
            PostCommentTopBar(
                authorName = authorName,
                onBack = onBack
            )
        },
        bottomBar = {
            CommentInputBar(
                value = commentText,
                onValueChange = onCommentTextChange,
                onSend = onSendComment,
                sending = sending
            )
        }
    ) { innerPadding ->
        PostCommentBody(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                ),
            post = post,
            comments = comments,
            loading = loading,
            onToggleLike = onToggleLike,
            onToggleSave = onToggleSave
        )
    }
}
