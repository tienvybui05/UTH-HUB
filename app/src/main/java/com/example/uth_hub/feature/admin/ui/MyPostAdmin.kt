package com.example.uth_hub.feature.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uth_hub.feature.admin.ui.components.EmptyPostsState
import com.example.uth_hub.feature.post.ui.component.PostCommentTopBar
import com.example.uth_hub.feature.post.ui.component.PostItem
import com.example.uth_hub.feature.post.viewmodel.FeedViewModel
import com.example.uth_hub.feature.post.viewmodel.FeedViewModelFactory
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.admin.ui.components.LoadingSkeleton
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostAdmin(
    navController: NavController,
    vm: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            PostDI.providePostRepository(),
            PostDI.auth
        )
    )
) {
    val posts by vm.posts.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    // Lấy user ID
    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    // Lọc danh sách bài viết
    val myPosts = remember(posts, currentUserId) {
        posts.filter { it.authorId == currentUserId }
    }

    Scaffold(
        topBar = {
            PostCommentTopBar(
                authorName = "bạn",
                onBack = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {

            when {
                isLoading -> LoadingSkeleton()

                myPosts.isEmpty() -> {
                    EmptyPostsState(selectedInstitute = "của bạn")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        items(myPosts) { post ->
                            PostItem(
                                postModel = post,
                                onLike = {
                                    vm.toggleLike(
                                        postId = post.id,
                                        postAuthorId = post.authorId
                                    )
                                },
                                onComment = {
                                    navController.navigate(
                                        "${com.example.uth_hub.app.navigation.Routes.PostComment}/${post.id}"
                                    )
                                },
                                onSave = {
                                    vm.toggleSave(post.id)
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}
