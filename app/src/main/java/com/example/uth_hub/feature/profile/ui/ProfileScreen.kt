package com.example.uth_hub.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.app.navigation.AuthRoutes
import com.example.uth_hub.app.navigation.Routes
import com.example.uth_hub.feature.profile.ui.components.ProfileHeader
import com.example.uth_hub.feature.profile.ui.components.ProfileTabBar
import com.example.uth_hub.feature.profile.ui.components.SettingsSheet
import com.example.uth_hub.feature.profile.ui.components.ShareProfileSheet
import com.example.uth_hub.feature.profile.ui.components.TopBarSimple
import com.example.uth_hub.feature.profile.ui.components.ChangeAvatarSheet
import com.example.uth_hub.feature.profile.util.rememberAvatarPicker
import com.example.uth_hub.feature.profile.viewmodel.ProfileViewModel
import com.example.uth_hub.feature.deeplink.AppLinkConfig

// 🔹 dùng lại các component mình đã tạo
import com.example.uth_hub.feature.profile.ui.components.FullScreenImageDialog
import com.example.uth_hub.feature.profile.ui.components.ProfileMediaTab
import com.example.uth_hub.feature.profile.ui.components.rememberUserPosts

// 🔹 dùng PostItem & FeedViewModel giống HomeScreen
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.ui.component.PostItem
import com.example.uth_hub.feature.post.viewmodel.FeedViewModel
import com.example.uth_hub.feature.post.viewmodel.FeedViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun Profile(navController: NavController, vm: ProfileViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }      // sheet cài đặt
    var showChangeAvatar by remember { mutableStateOf(false) }  // sheet đổi avatar
    var showShareProfile by remember { mutableStateOf(false) }

    // viewer ảnh full-screen
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    val ui = vm.ui.collectAsState().value
    val user = ui.user

    // 🔹 lấy posts cho tab "File phương tiện" (grid ảnh)
    val mediaPostsState by rememberUserPosts(userId = user?.uid.orEmpty())

    // 🔹 ViewModel feed giống HomeScreen để lấy danh sách bài viết & toggle like/save
    val feedVm: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            PostDI.providePostRepository(),
            PostDI.auth
        )
    )
    val allPosts by feedVm.posts.collectAsState()

    // 🔹 chỉ giữ lại bài viết của chính user này
    val userPosts = remember(allPosts, user?.uid) {
        val uid = user?.uid
        if (uid == null) emptyList() else allPosts.filter { it.authorId == uid }
    }

    // 🔹 repo để gọi reportPost
    val scope = rememberCoroutineScope()
    val postRepo = remember { PostDI.providePostRepository() }

    // role: nếu là admin thì chuyển sang màn admin profile
    val userRole = ui.user?.role ?: "student"
    LaunchedEffect(userRole) {
        if (userRole == "admin") {
            navController.navigate(Routes.ManagerProfile) {
                popUpTo(0)
            }
        }
    }

    // Avatar picker: nhận uri / bitmap và đẩy xuống ViewModel
    val avatarPicker = rememberAvatarPicker(
        onGalleryImagePicked = { uri ->
            if (uri != null) {
                vm.updateAvatarFromUri(uri)
            }
        },
        onCameraImageTaken = { bitmap ->
            if (bitmap != null) {
                vm.updateAvatarFromBitmap(bitmap)
            }
        }
    )

    Scaffold(
        topBar = {
            TopBarSimple(
                onBackClick = { navController.navigateUp() },
                onMenuClick = { showSettings = true }
            )
        },
    ) { innerPadding ->

        // *** VIEWER ẢNH FULL-SCREEN ***
        FullScreenImageDialog(
            imageUrl = selectedImageUrl,
            onDismiss = { selectedImageUrl = null }
        )

        // *** SHEET CÀI ĐẶT ***
        if (showSettings) {
            SettingsSheet(
                onDismissRequest = { showSettings = false },

                //  Đã lưu
                onGoSaved = {
                    showSettings = false
                    navController.navigate(Routes.SavedPost)
                },

                //  Đã thích
                onGoLiked = {
                    showSettings = false
                    navController.navigate(Routes.LikedPost)
                },

                //  Thay đổi ảnh đại diện
                onGoChangeAvatar = {
                    showSettings = false
                    showChangeAvatar = true
                },

                // Đổi mật khẩu
                onGoChangePw = {
                    showSettings = false
                    navController.navigate(Routes.ChangePassword)
                },

                // Điều khoản
                onGoTerms = {
                    showSettings = false
                    navController.navigate(Routes.AboutTerms)
                },

                // Logout
                onLogout = {
                    showSettings = false
                    vm.signOut()
                    navController.navigate(AuthRoutes.SignIn) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }
        // *** SHEET CHIA SẺ TRANG CÁ NHÂN ***
        if (showShareProfile && user != null) {
            ShareProfileSheet(
                usernameOrMssv = user.displayName,
                profileUrl = AppLinkConfig.buildProfileUrl(user.uid),
                onDismissRequest = { showShareProfile = false }
            )
        }

        // *** SHEET ĐỔI AVATAR ***
        if (showChangeAvatar) {
            ChangeAvatarSheet(
                onPickFromGallery = {
                    avatarPicker.openGallery()
                },
                onTakePhoto = {
                    avatarPicker.openCamera()
                },
                onRemove = {
                    vm.resetAvatarToGoogleDefault()
                },
                onDismiss = {
                    showChangeAvatar = false
                }
            )
        }

        if (ui.loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val currentUser = ui.user
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProfileHeader(
                    name = currentUser?.displayName ?: "—",
                    username = currentUser?.mssv ?: "—",
                    major = currentUser?.institute ?: "—",
                    code = currentUser?.classCode ?: "—",
                    avatarUrl = currentUser?.photoUrl,
                    isOwner = true,
                    onEditClick = {
                        navController.navigate(Routes.EditProfile)
                    },
                    onShareClick = { showShareProfile = true }
                )
            }

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
                            Text("Chưa có bài viết", color = Color.White)
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
                                        // dùng y chang HomeScreen
                                        navController.navigate("${Routes.PostComment}/${p.id}")
                                    },
                                    onSave = { feedVm.toggleSave(p.id) },
                                    onReport = {
                                        scope.launch {
                                            try {
                                                postRepo.reportPost(p.id)
                                            } catch (_: Exception) {
                                            }
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

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    val navController = rememberNavController()
    Profile(navController)
}
