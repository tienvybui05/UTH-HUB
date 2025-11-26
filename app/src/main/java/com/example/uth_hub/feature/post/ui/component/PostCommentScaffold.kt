package com.example.uth_hub.feature.post.ui.component

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.example.uth_hub.feature.post.domain.model.PostModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
    onSetMedia: (List<Uri>, String) -> Unit,   // "image" hoặc "video"
    onClearMedia: () -> Unit,
    onCommentLike: (CommentModel) -> Unit,
    onReplyClick: (CommentModel) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    val context = LocalContext.current

    //  Chọn ảnh / video (1 icon media)
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            val first = uris.first()
            val mime = context.contentResolver.getType(first) ?: ""
            val isVideo = mime.startsWith("video")

            if (isVideo) {
                // Nếu là video → chỉ giữ video đầu tiên
                onSetMedia(listOf(first), "video")
            } else {
                // Nếu là ảnh → cho chọn nhiều
                onSetMedia(uris, "image")
            }
        }
    }

    //  Mở camera (icon camera)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val uri = saveBitmapToCache(context, bitmap)
            if (uri != null) {
                onSetMedia(listOf(uri), "image")
            }
        }
    }

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
                //  icon media: mở gallery chọn ảnh / video
                onPickMedia = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageAndVideo
                        )
                    )
                },
                //  icon camera: mở camera chụp ảnh
                onOpenCamera = {
                    cameraLauncher.launch(null)
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

// Lưu ảnh camera vào cache để có Uri upload lên Firebase Storage
private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val file = File(context.cacheDir, "comment_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        Uri.fromFile(file)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
