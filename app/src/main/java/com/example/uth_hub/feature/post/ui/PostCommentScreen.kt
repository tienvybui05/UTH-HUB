package com.example.uth_hub.feature.post.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uth_hub.app.navigation.Routes
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.ui.component.PostCommentScaffold
import com.example.uth_hub.feature.post.viewmodel.CommentsViewModel
import com.example.uth_hub.feature.post.viewmodel.CommentsViewModelFactory

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
    val mediaUris by vm.commentMediaUris.collectAsState()
    val mediaType by vm.commentMediaType.collectAsState()

    // Đồng bộ commentCount theo số lượng comments hiện có (realtime)
    val postForUi = remember(post, comments) {
        post?.copy(
            commentCount = comments.size.toLong()
        )
    }

    PostCommentScaffold(
        authorName = postForUi?.authorName ?: "",
        post = postForUi,
        comments = comments,
        commentText = commentText,
        loading = loading,
        sending = sending,
        mediaUris = mediaUris,
        mediaType = mediaType,
        onBack = { navController.popBackStack() },
        onCommentTextChange = vm::onCommentTextChange,
        onSendComment = vm::sendComment,
        onToggleLike = {
            post?.let { p ->
                vm.toggleLike(
                    postId = p.id,
                    postAuth = p.authorId
                )
            }
        },
        onToggleSave = vm::toggleSave,
        onSetMedia = vm::setMedia,
        onClearMedia = vm::clearMedia,
        onCommentLike = vm::toggleCommentLike,
        onReplyClick = vm::startReplyTo,
        onOpenProfile = { uid ->
            navController.navigate("${Routes.OtherProfile}/$uid")
        },
        onEditComment = vm::startEditComment,
        onDeleteComment = vm::deleteComment
    )
}
