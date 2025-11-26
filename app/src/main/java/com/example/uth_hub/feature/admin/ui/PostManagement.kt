package com.example.uth_hub.feature.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uth_hub.app.navigation.Routes
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.admin.ui.components.AdminLoadingSkeleton
import com.example.uth_hub.feature.admin.ui.components.EmptyPostsState // ← Import từ components
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.viewmodel.FeedViewModel
import com.example.uth_hub.feature.post.viewmodel.FeedViewModelFactory
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ChevronLeft

// Danh sách các viện/khoa
private val INSTITUTES = listOf(
    "Tất cả khoa",
    "Viện CNTT & Điện, điện tử",
    "Viện Cơ khí",
    "Viện Đường sắt tốc độ cao",
    "Viện Kinh tế & Phát triển Giao thông Vận tải",
    "Viện Hàng hải",
    "Viện Ngôn ngữ, Khoa học Chính trị & Xã hội",
    "Viện Nghiên cứu & Đào tạo Đèo Cả"
)

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
    val isLoading by vm.isLoading.collectAsState() // ← Lấy trạng thái loading từ ViewModel
    var selectedInstitute by remember { mutableStateOf("Tất cả khoa") }
    var expanded by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) } // ← Loading riêng cho xóa bài
    var showMessage by remember { mutableStateOf<String?>(null) }
    var postToDelete by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val filteredPosts = remember(posts, selectedInstitute) {
        if (selectedInstitute == "Tất cả khoa") {
            posts
        } else {
            posts.filter { it.authorInstitute == selectedInstitute }
        }
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
                println("Bắt đầu xử lý xóa bài viết: $postId")
                vm.deletePost(postId)
                showMessage = " Đã xóa bài viết thành công"
                println("Xóa bài viết $postId thành công từ UI")
            } catch (e: Exception) {
                val errorMsg = "Lỗi khi xóa bài viết: ${e.message}"
                showMessage = errorMsg
                println(errorMsg)
            } finally {
                isDeleting = false
                postToDelete = null
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            PostManagementTopBar(
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Filter Section
                FilterSection(
                    selectedInstitute = selectedInstitute,
                    expanded = expanded,
                    onInstituteClick = { expanded = true },
                    onInstituteSelected = { institute ->
                        selectedInstitute = institute
                        expanded = false
                    },
                    onDismissRequest = { expanded = false },
                    onReportedPostsClick = { navController.navigate(Routes.ReportedPost) }
                )

                // Thống kê - chỉ hiển thị khi không loading
                if (!isLoading) {
                    StatisticsSection(
                        filteredPostsCount = filteredPosts.size,
                        selectedInstitute = selectedInstitute
                    )
                }

                // Posts List với loading state
                if (isLoading) {
                    // Hiển thị skeleton loading khi đang tải dữ liệu
                    AdminLoadingSkeleton()
                } else if (filteredPosts.isEmpty()) {
                    // Sử dụng EmptyPostsState từ components
                    EmptyPostsState(selectedInstitute = selectedInstitute)
                } else {
                    PostsListSection(
                        filteredPosts = filteredPosts,
                        isDeleting = isDeleting,
                        onDeletePost = { postId -> postToDelete = postId },
                        onViewReports = { postId ->
                            println("Xem báo cáo bài viết: $postId")
                        },
                        onLike = { postId -> vm.toggleLike(postId) },
                        onComment = { postId ->
                            navController.navigate("${Routes.PostComment}/${postId}")
                        },
                        onSave = { postId -> vm.toggleSave(postId) },
                        navController = navController
                    )
                }
            }

            // Loading indicator cho xóa bài
            if (isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ColorCustom.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostManagementTopBar(
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Quản lý bài viết",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorCustom.primary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.ChevronLeft,
                    contentDescription = "Quay về",
                    tint = ColorCustom.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
private fun FilterSection(
    selectedInstitute: String,
    expanded: Boolean,
    onInstituteClick: () -> Unit,
    onInstituteSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onReportedPostsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown chọn khoa
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onInstituteClick() }
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                        ),
                    shape = RoundedCornerShape(12.dp),
                    color = ColorCustom.primary
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedInstitute,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Chọn khoa",
                            tint = Color.White
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = onDismissRequest,
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth(0.9f)
                ) {
                    INSTITUTES.forEach { institute ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = institute,
                                    color = if (institute == selectedInstitute) ColorCustom.primary else Color.Black,
                                    fontWeight = if (institute == selectedInstitute) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = { onInstituteSelected(institute) }
                        )
                    }
                }
            }

            // Nút bài viết bị tố cáo
            Button(
                onClick = onReportedPostsClick,
                modifier = Modifier,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorCustom.linkPink
                )
            ) {
                Text(
                    text = "Bài bị tố cáo",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StatisticsSection(
    filteredPostsCount: Int,
    selectedInstitute: String
) {
    Text(
        text = buildString {
            append("Hiển thị $filteredPostsCount bài viết")
            if (selectedInstitute != "Tất cả khoa") {
                append(" từ $selectedInstitute")
            }
        },
        fontSize = 14.sp,
        color = ColorCustom.secondText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@Composable
private fun PostsListSection(
    filteredPosts: List<com.example.uth_hub.feature.post.domain.model.PostModel>,
    isDeleting: Boolean,
    onDeletePost: (String) -> Unit,
    onViewReports: (String) -> Unit,
    onLike: (String) -> Unit,
    onComment: (String) -> Unit,
    onSave: (String) -> Unit,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filteredPosts.size) { index ->
            val post = filteredPosts[index]
            AdminPostItem(
                post = post,
                onDeletePost = onDeletePost,
                onViewReports = onViewReports,
                onLike = onLike,
                onComment = onComment,
                onSave = onSave,
                navController = navController,
                isLoading = isDeleting
            )
        }
    }
}