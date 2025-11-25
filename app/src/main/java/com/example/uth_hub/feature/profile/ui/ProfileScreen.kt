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
import com.example.uth_hub.feature.profile.ui.components.TopBarSimple
import com.example.uth_hub.feature.profile.ui.components.ChangeAvatarSheet
import com.example.uth_hub.feature.profile.util.rememberAvatarPicker
import com.example.uth_hub.feature.profile.viewmodel.ProfileViewModel
import com.example.uth_hub.feature.auth.AuthConst
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun Profile(navController: NavController, vm: ProfileViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }      // sheet cài đặt
    var showChangeAvatar by remember { mutableStateOf(false) }  // sheet đổi avatar

    val ui = vm.ui.collectAsState().value

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

                // 👉 Thay đổi ảnh đại diện
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

        val user = ui.user
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProfileHeader(
                    name = user?.displayName ?: "—",
                    username = user?.mssv ?: "—",
                    major = user?.institute ?: "—",
                    code = user?.classCode ?: "—",
                    avatarUrl = user?.photoUrl,
                    isOwner = true,
                    onEditClick = { /* TODO: sửa thông tin */ },
                    onShareClick = { /* TODO: chia sẻ profile */ }
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
                0 -> item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Chưa có bài viết", color = Color.White)
                    }
                }

                1 -> item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Chưa có ảnh/video", color = Color.White)
                    }
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
