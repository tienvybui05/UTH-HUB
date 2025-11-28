package com.example.uth_hub.feature.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uth_hub.feature.admin.ui.components.InstituteDropdown
import com.example.uth_hub.feature.admin.ui.components.PostManagementTopBar
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.viewmodel.FeedViewModel
import com.example.uth_hub.feature.post.viewmodel.FeedViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportedPost(
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

    // Lọc bài viết bị báo cáo + theo khoa
    val filteredPosts = remember(posts, selectedInstitute) {
        posts.filter { it.reportCount > 0 }
            .filter { selectedInstitute == "Tất cả khoa" || it.authorInstitute == selectedInstitute }
    }

    // Snackbar
    LaunchedEffect(showMessage) {
        showMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            showMessage = null
        }
    }

    // Xử lý xóa post
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PostManagementTopBar(
                title = "Bài viết bị báo cáo",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Dropdown lọc khoa (dùng lại component sẵn có)
            InstituteDropdown(
                selectedInstitute = selectedInstitute,
                onInstituteSelected = { selectedInstitute = it },
                isAdminStyle = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )

            // Nội dung
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (filteredPosts.isEmpty()) {
                    Text(
                        text = "Không có bài viết bị báo cáo",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(filteredPosts, key = { it.id }) { post ->
                            AdminPostItem(
                                post = post,
                                onDeletePost = { postToDelete = it },
                                onViewReports = { /* mở chi tiết báo cáo nếu muốn */ },
                                navController = navController,
                                isLoading = isDeleting
                            )
                        }
                    }
                }
            }
        }
    }
}
