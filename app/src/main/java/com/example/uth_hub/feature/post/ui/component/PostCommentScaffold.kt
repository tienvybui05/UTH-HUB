package com.example.uth_hub.feature.post.ui.component

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.example.uth_hub.feature.post.domain.model.CommentModel
import com.example.uth_hub.feature.post.domain.model.PostModel
import androidx.media3.common.util.UnstableApi

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
    onOpenProfile: (String) -> Unit,
    // NEW: callback sửa / xóa comment (ViewModel sẽ truyền vào)
    onEditComment: (CommentModel) -> Unit,
    onDeleteComment: (CommentModel) -> Unit
) {
    val context = LocalContext.current

    // comment đang được user chọn để trả lời
    var replyTarget by remember { mutableStateOf<CommentModel?>(null) }

    // comment đang được mở menu action (sửa / xóa / hủy)
    var actionComment by remember { mutableStateOf<CommentModel?>(null) }
    var showActionSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ảnh / video đang được xem full-screen
    var previewImageUrl by remember { mutableStateOf<String?>(null) }
    var previewVideoUrl by remember { mutableStateOf<String?>(null) }

    // click "Trả lời" trên 1 comment
    val handleReplyClick: (CommentModel) -> Unit = { comment ->
        replyTarget = comment
        onReplyClick(comment)
    }

    // long-press 1 comment -> mở sheet
    val handleLongClick: (CommentModel) -> Unit = { comment ->
        actionComment = comment
        showActionSheet = true
    }

    // Khi chọn trả lời và ô comment đang trống -> tự thêm tên người đó vào text
    LaunchedEffect(replyTarget?.id) {
        val target = replyTarget
        if (target != null && commentText.isBlank()) {
            onCommentTextChange(target.authorName + " ")
        }
    }

    //  Chọn 1 ảnh hoặc 1 video (giống Facebook: 1 media mỗi lần)
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val mime = context.contentResolver.getType(uri) ?: ""
            val isVideo = mime.startsWith("video")

            if (isVideo) {
                onSetMedia(listOf(uri), "video")
            } else {
                onSetMedia(listOf(uri), "image")
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
                onClearMedia = onClearMedia,
                // hiển thị thanh "Đang trả lời ..."
                replyToAuthorName = replyTarget?.authorName,
                onClickReplyAuthor = {
                    replyTarget?.authorId?.let { onOpenProfile(it) }
                },
                onCancelReply = {
                    replyTarget = null
                }
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
            replyingCommentId = replyTarget?.id, // truyền xuống để highlight comment
            loading = loading,
            onToggleLike = onToggleLike,
            onToggleSave = onToggleSave,
            onCommentLike = onCommentLike,
            onReplyClick = handleReplyClick,
            onOpenProfile = onOpenProfile,
            onCommentLongClick = handleLongClick,
            // NEW: click vào media trong comment
            onImageClick = { url ->
                previewVideoUrl = null
                previewImageUrl = url
            },
            onVideoClick = { url ->
                previewImageUrl = null
                previewVideoUrl = url
            }
        )
    }

    // ===== SHEET: XÓA / CHỈNH SỬA / HỦY =====
    if (showActionSheet && actionComment != null) {
        ModalBottomSheet(
            onDismissRequest = { showActionSheet = false },
            sheetState = sheetState
        ) {
            val c = actionComment!!

            SheetActionItem(
                text = "Chỉnh sửa"
            ) {
                showActionSheet = false
                replyTarget = null     // bỏ trạng thái trả lời nếu có
                onEditComment(c)
            }

            SheetActionItem(
                text = "Xóa",
                isDestructive = true
            ) {
                showActionSheet = false
                // nếu đang reply vào comment này thì clear luôn
                if (replyTarget?.id == c.id) replyTarget = null
                onDeleteComment(c)
            }

            SheetActionItem(
                text = "Hủy"
            ) {
                showActionSheet = false
            }
        }
    }

    // ===== VIEWER ẢNH FULL-SCREEN =====
    if (previewImageUrl != null) {
        FullscreenImageViewer(
            imageUrl = previewImageUrl!!,
            onDismiss = { previewImageUrl = null }
        )
    }

    // ===== VIEWER VIDEO FULL-SCREEN (CÓ ÂM THANH) =====
    if (previewVideoUrl != null) {
        FullscreenVideoPlayer(
            videoUrl = previewVideoUrl!!,
            onDismiss = { previewVideoUrl = null }
        )
    }
}

// Item trong bottom sheet
@Composable
private fun SheetActionItem(
    text: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = if (isDestructive) Color.Red else Color(0xFF007AFF)
        )
    }
}

// Viewer full-screen cho ảnh bình luận
@Composable
private fun FullscreenImageViewer(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun FullscreenVideoPlayer(
    videoUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    //  ExoPlayer đời Media3
    val exoPlayer = remember {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build()
    }

    // Load & play video
    LaunchedEffect(videoUrl) {
        runCatching {
            val mediaItem = androidx.media3.common.MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }.onFailure {
            // Nếu có lỗi thì dừng player để khỏi crash tiếp
            exoPlayer.playWhenReady = false
        }
    }

    // Giải phóng player khi đóng dialog
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Dialog(
        onDismissRequest = {
            exoPlayer.playWhenReady = false
            onDismiss()
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                factory = { ctx ->
                    // ✅ PlayerView của Media3
                    androidx.media3.ui.PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true          // có play/pause, seekbar
                        setShowBuffering(
                            androidx.media3.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING
                        )
                    }
                }
            )
        }
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
