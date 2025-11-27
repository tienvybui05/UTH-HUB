package com.example.uth_hub.feature.post.ui.component

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            onCommentLongClick = handleLongClick
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
