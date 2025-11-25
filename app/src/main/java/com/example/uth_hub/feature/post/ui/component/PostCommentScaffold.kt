package com.example.uth_hub.feature.post.ui.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    mediaUris: List<Uri>,
    mediaType: String?,
    onBack: () -> Unit,
    onCommentTextChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    onSetMedia: (List<Uri>, String?) -> Unit,
    onClearMedia: () -> Unit,
    onCommentLike: (CommentModel) -> Unit,
    onReplyClick: (CommentModel) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    // ===== PHOTO PICKER =====

    // Chọn nhiều ảnh
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (!uris.isNullOrEmpty()) {
                onSetMedia(uris, "image")
            }
        }
    )

    // Chọn 1 video
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { onSetMedia(listOf(it), "video") }
        }
    )

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
                sending = sending,
                mediaUris = mediaUris,
                mediaType = mediaType,
                onPickImages = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onPickVideo = {
                    videoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.VideoOnly
                        )
                    )
                },
                onClearMedia = onClearMedia
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
            onToggleSave = onToggleSave,
            onCommentLike = onCommentLike,
            onReplyClick = onReplyClick,
            onOpenProfile = onOpenProfile
        )
    }
}


