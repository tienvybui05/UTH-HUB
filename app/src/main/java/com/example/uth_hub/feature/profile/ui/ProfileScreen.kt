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
import com.example.uth_hub.feature.profile.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Profile(navController: NavController, vm: ProfileViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }   // 👈 trạng thái mở sheet
    val ui = vm.ui.collectAsState().value
    val userRole = ui.user?.role ?: "student"
    LaunchedEffect(userRole) {
        if (userRole == "admin") {
            navController.navigate("managerProfile") {
                popUpTo(0)
            }
        }
    }
    Scaffold(
        topBar = {
            TopBarSimple(
                onBackClick = { navController.navigateUp() },
                onMenuClick = { showSettings = true }          // 👈 mở sheet khi bấm dấu ba chấm
            )
        },
    ) { innerPadding ->

        // *** SHEET CÀI ĐẶT ***
        if (showSettings) {
            SettingsSheet(
                onDismissRequest = { showSettings = false },
                // ✅ thêm callback điều hướng sang màn hình đổi mật khẩu
                onGoChangePw = {
                    showSettings = false
                    navController.navigate(Routes.ChangePassword)
                },
                onLogout = {
                    showSettings = false
                    vm.signOut()
                    // quay về màn đăng nhập & xoá backstack
                    navController.navigate(AuthRoutes.SignIn) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
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
