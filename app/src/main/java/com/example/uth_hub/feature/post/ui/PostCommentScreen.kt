package com.example.uth_hub.feature.post.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.viewmodel.CommentsViewModel
import com.example.uth_hub.feature.post.viewmodel.CommentsViewModelFactory
import com.example.uth_hub.feature.post.ui.component.PostCommentScaffold

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

    PostCommentScaffold(
        authorName = post?.authorName ?: "",
        post = post,
        comments = comments,
        commentText = commentText,
        loading = loading,
        sending = sending,
        onBack = { navController.popBackStack() },
        onCommentTextChange = vm::onCommentTextChange,
        onSendComment = vm::sendComment,
        onToggleLike = vm::toggleLike,
        onToggleSave = vm::toggleSave
    )
}
