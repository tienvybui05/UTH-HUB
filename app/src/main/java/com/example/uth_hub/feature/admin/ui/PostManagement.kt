package com.example.uth_hub.feature.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uth_hub.feature.admin.ui.components.PostManagementContent
import com.example.uth_hub.feature.admin.ui.components.PostManagementTopBar
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.viewmodel.FeedViewModel
import com.example.uth_hub.feature.post.viewmodel.FeedViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostManagement(
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
    var selectedInstitute by remember { mutableStateOf("Tất cả khoa") }
    var isDeleting by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf<String?>(null) }
    var postToDelete by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val filteredPosts = remember(posts, selectedInstitute) {
        if (selectedInstitute == "Tất cả khoa") posts
        else posts.filter { it.authorInstitute == selectedInstitute }
    }

    // Hiển thị Snackbar khi có message
    LaunchedEffect(showMessage) {
        showMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            showMessage = null
        }
    }

    // Xử lý xóa bài viết
    LaunchedEffect(postToDelete) {
        postToDelete?.let { postId ->
            isDeleting = true
            try {
                vm.deletePost(postId)
                showMessage = "Đã xóa bài viết thành công"
            } catch (e: Exception) {
                showMessage = "Lỗi khi xóa bài viết: ${e.message}"
            } finally {
                isDeleting = false
                postToDelete = null
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            PostManagementTopBar(onBackClick = { navController.popBackStack() })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            PostManagementContent(
                isLoading = isLoading,
                isDeleting = isDeleting,
                selectedInstitute = selectedInstitute,
                filteredPosts = filteredPosts,
                onInstituteSelected = { selectedInstitute = it },
                onReportedPostsClick = { navController.navigate(com.example.uth_hub.app.navigation.Routes.ReportedPost) },
                onDeletePost = { postToDelete = it },
                onLike = { postId -> vm.toggleLike(postId) },
                onComment = { postId -> navController.navigate("${com.example.uth_hub.app.navigation.Routes.PostComment}/${postId}") },
                onSave = { postId -> vm.toggleSave(postId) },
                navController = navController
            )
        }
    }
}