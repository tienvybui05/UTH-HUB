package com.example.uth_hub.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uth_hub.feature.profile.viewmodel.OtherProfileUiState
import com.example.uth_hub.feature.profile.viewmodel.OtherProfileViewModel
import com.example.uth_hub.feature.profile.viewmodel.OtherProfileViewModelFactory
import com.example.uth_hub.feature.profile.ui.components.ProfileHeader
import com.example.uth_hub.feature.profile.ui.components.ProfileTabBar
import com.example.uth_hub.feature.profile.ui.components.TopBarSimple
import com.example.uth_hub.feature.profile.ui.components.ShareProfileSheet

// dùng lại components media + viewer
import com.example.uth_hub.feature.profile.ui.components.FullScreenImageDialog
import com.example.uth_hub.feature.profile.ui.components.ProfileMediaTab
import com.example.uth_hub.feature.profile.ui.components.rememberUserPosts

// dùng PostItem & FeedViewModel như HomeScreen
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.ui.component.PostItem
import com.example.uth_hub.feature.post.viewmodel.FeedViewModel
import com.example.uth_hub.feature.post.viewmodel.FeedViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.uth_hub.app.navigation.Routes

@Composable
fun OtherProfileScreen(
    navController: NavController,
    uid: String,
) {
    val vm: OtherProfileViewModel = viewModel(
        factory = OtherProfileViewModelFactory(uid)
    )

    var selectedTabIndex by remember { mutableStateOf(0) }

    // ⭐ THÊM STATE MỞ SHARE SHEET
    var showShareProfile by remember { mutableStateOf(false) }

    // ⭐ FULLSCREEN IMAGE VIEWER
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    val ui: OtherProfileUiState = vm.ui.collectAsState().value
    val user = ui.user

    // ⭐ URL dành cho người khác
    val profileUrl = "https://uth-hub-49b77.web.app/user/$uid"

    // 🔹 posts cho tab media (grid ảnh)
    val mediaPostsState by rememberUserPosts(userId = uid)

    // 🔹 FeedViewModel để lấy tất cả bài viết và filter theo uid này
    val feedVm: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            PostDI.providePostRepository(),
            PostDI.auth
        )
    )
    val allPosts by feedVm.posts.collectAsState()
    val userPosts = remember(allPosts, uid) {
        allPosts.filter { it.authorId == uid }
    }

    val scope = rememberCoroutineScope()
    val postRepo = remember { PostDI.providePostRepository() }

    Scaffold(
        topBar = {
            TopBarSimple(
                onBackClick = { navController.navigateUp() },
                onMenuClick = { }
            )
        },
    ) { innerPadding ->

        if (ui.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // ⭐ FULLSCREEN IMAGE
        FullScreenImageDialog(
            imageUrl = selectedImageUrl,
            onDismiss = { selectedImageUrl = null }
        )

        // ⭐ SHOW BOTTOM SHEET
        if (showShareProfile) {
            ShareProfileSheet(
                profileUrl = profileUrl,
                usernameOrMssv = user?.mssv ?: uid,
                onDismissRequest = { showShareProfile = false }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // HEADER
            item {
                ProfileHeader(
                    name = user?.displayName ?: "—",
                    username = user?.mssv ?: "—",
                    major = user?.institute ?: "—",
                    code = user?.classCode ?: "—",
                    avatarUrl = user?.photoUrl,
                    isOwner = false,
                    onShareClick = { showShareProfile = true }
                )
            }

            // TAB BAR
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF008689))
                ) {
                    ProfileTabBar(
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it }
                    )
                }
            }

            item { Spacer(Modifier.height(10.dp)) }

            when (selectedTabIndex) {

                // ====================
                //   TAB BÀI ĐĂNG
                // ====================
                0 -> item {
                    if (userPosts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Chưa có bài đăng", color = Color.White)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            userPosts.forEach { p ->
                                PostItem(
                                    postModel = p,
                                    onLike = { feedVm.toggleLike(p.id, p.authorId) },
                                    onComment = {
                                        navController.navigate("${Routes.PostComment}/${p.id}")
                                    },
                                    onSave = { feedVm.toggleSave(p.id) },
                                    onReport = {
                                        scope.launch {
                                            try {
                                                postRepo.reportPost(p.id)
                                            } catch (_: Exception) { }
                                        }
                                    },
                                    onImageClick = { url ->
                                        selectedImageUrl = url
                                    }
                                )
                            }
                        }
                    }
                }

                // ====================
                //   TAB FILE PHƯƠNG TIỆN
                // ====================
                1 -> item {
                    ProfileMediaTab(
                        state = mediaPostsState,
                        onImageClick = { url -> selectedImageUrl = url }
                    )
                }
            }

            item { Spacer(Modifier.height(60.dp)) }
        }
    }
}
