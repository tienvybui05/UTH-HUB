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
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.core.design.theme.ColorCustom
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
    var selectedInstitute by remember { mutableStateOf("Tất cả khoa") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
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
            snackbarHostState.showSnackbar(message)
            showMessage = null
        }
    }

    // Xử lý xóa bài viết
    LaunchedEffect(postToDelete) {
        postToDelete?.let { postId ->
            isLoading = true
            try {
                println("🔄 Bắt đầu xử lý xóa bài viết: $postId")
                vm.deletePost(postId)
                showMessage = "Đã xóa bài viết thành công"
                println("✅ Xóa bài viết $postId thành công từ UI")
            } catch (e: Exception) {
                val errorMsg = "Lỗi khi xóa bài viết: ${e.message}"
                showMessage = errorMsg
                println("❌ $errorMsg")
            } finally {
                isLoading = false
                postToDelete = null
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp, 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.ChevronLeft,
                            contentDescription = "Quay về",
                            tint = ColorCustom.primary,
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            "Quản lý bài viết",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorCustom.primary
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = ColorCustom.primary)
                )

                // Filter Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dropdown chọn khoa
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = true }
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(12.dp),
                                    ),
                                shape = RoundedCornerShape(10.dp),
                                color = ColorCustom.primary
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedInstitute,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
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
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(Color.White)
                                    .fillMaxWidth(0.8f)
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
                                        onClick = {
                                            selectedInstitute = institute
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            navController.navigate("reported_posts")
                        },
                        modifier = Modifier,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorCustom.linkPink
                        )
                    ) {
                        Text(text = "Bị tố cáo", color = Color.White)
                    }
                }

                // Thống kê
                Text(
                    text = "Hiển thị ${filteredPosts.size} bài viết" +
                            if (selectedInstitute != "Tất cả khoa") " từ $selectedInstitute" else "",
                    fontSize = 14.sp,
                    color = ColorCustom.secondText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                // Posts List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPosts.size) { index ->
                        AdminPostItem(
                            post = filteredPosts[index],
                            onDeletePost = { postId ->
                                postToDelete = postId
                            },
                            onViewReports = { postId ->
                                println("Xem báo cáo bài viết: $postId")
                            },
                            isLoading = isLoading
                        )
                    }

                    if (filteredPosts.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (selectedInstitute == "Tất cả khoa") {
                                        "Chưa có bài viết nào"
                                    } else {
                                        "Không có bài viết nào từ $selectedInstitute"
                                    },
                                    fontSize = 16.sp,
                                    color = ColorCustom.secondText,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}