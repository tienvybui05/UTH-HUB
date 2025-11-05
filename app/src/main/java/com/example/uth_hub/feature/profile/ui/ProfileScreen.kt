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
import com.example.uth_hub.app.navigation.BottomNavigationBar
import com.example.uth_hub.feature.profile.ui.components.ProfileHeader
import com.example.uth_hub.feature.profile.ui.components.ProfileTabBar
import com.example.uth_hub.feature.profile.ui.components.TopBarSimple
import com.example.uth_hub.feature.profile.viewmodel.ProfileViewModel

@Composable
fun Profile(navController: NavController, vm: ProfileViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val ui = vm.ui.collectAsState().value

    Scaffold(
        topBar = {
            TopBarSimple(
                onBackClick = { navController.navigateUp() },
                onMenuClick = {
                    // ví dụ: đăng xuất & quay về SignIn
                    vm.signOut()
                    navController.popBackStack(route = "auth/signin", inclusive = false)
                },
            )
        },
        bottomBar = { BottomNavigationBar(navController) },
    ) { innerPadding ->

        if (ui.loading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
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
                    avatarUrl = user?.photoUrl,   // nếu ProfileHeader hỗ trợ url, truyền vào
                    onEditClick = { /* TODO: mở màn edit info sau */ },
                    onShareClick = { /* TODO */ }
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

            // Chưa làm phần Post nên tạm để khối trống thân thiện
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
