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

@Composable
fun OtherProfileScreen(
    navController: NavController,
    uid: String,
) {
    val vm: OtherProfileViewModel = viewModel(
        factory = OtherProfileViewModelFactory(uid)
    )

    val ui: OtherProfileUiState = vm.ui.collectAsState().value
    val user = ui.user

    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopBarSimple(
                onBackClick = { navController.navigateUp() },
                onMenuClick = { }
            )
        }
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
                    onShareClick = { vm.shareProfile(user) }
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

                // TAB 0 – chỉ hiển thị text placeholder
                0 -> item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Chưa có bài đăng", color = Color.White)
                    }
                }

                // TAB 1 – media
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
