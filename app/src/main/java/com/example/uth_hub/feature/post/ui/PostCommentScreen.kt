package com.example.uth_hub.feature.post.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    PostCommentScaffold(
        authorName = post?.authorName ?: "",
        post = post,
        comments = comments,
        commentText = commentText,
        loading = loading,
        sending = sending,
        mediaUris = mediaUris,
        mediaType = mediaType,
        onBack = { navController.popBackStack() },
        onCommentTextChange = vm::onCommentTextChange,
        onSendComment = vm::sendComment,
        onToggleLike = vm::toggleLike,
        onToggleSave = vm::toggleSave,
        onSetMedia = vm::setCommentMedia,
        onClearMedia = vm::clearCommentMedia,
        onCommentLike = vm::toggleCommentLike,
        onReplyClick = vm::setReplyingTo,
        onOpenProfile = { uid ->
            navController.navigate("${Routes.OtherProfile}/$uid")
        }
    )
}
