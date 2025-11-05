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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.app.navigation.BottomNavigationBar
import com.example.uth_hub.feature.profile.ui.components.ProfileHeader
import com.example.uth_hub.feature.profile.ui.components.TopBarSimple
import com.example.uth_hub.core.design.components.Post
import com.example.uth_hub.feature.profile.ui.components.ProfileTabBar

@Composable
fun Profile(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopBarSimple(
                onBackClick = { /* TODO */ },
                onMenuClick = { /* TODO */ },
            )
        },
        bottomBar = { BottomNavigationBar(navController) },
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Header (gộp nền vào bên trong) ---
            item {
                ProfileHeader(
                    name = "Đạt Vỹ Lượng",
                    username = "anhdeptraio4",
                    major = "Viện CNTT & Điện, điện tử",
                    code = "CN2301C",
                    onEditClick = {},
                    onShareClick = {}
                )
            }

            // --- Sticky TabBar ---
            stickyHeader {
                // Phải có nền để khi dính lên top không bị xuyên nền
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

            // --- Nội dung theo tab ---
            item { Spacer(modifier = Modifier.height(10.dp)) }

            // các bài đăng, ảnh, ... cách nhau 10dp
            when (selectedTabIndex) {
                0 -> items(15) { index ->
                    // 🔹 Bọc mỗi Post trong padding ngang
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp) // ✅ padding 2 bên
                    ) {
                        Post()
                    }

                    if (index != 4) Spacer(modifier = Modifier.height(10.dp))
                }

                1 -> items(20) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp) // ✅ padding 2 bên
                            .height(150.dp)
                            .background(Color(0x22FFFFFF))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🖼 Ảnh / Video ${index + 1}", color = Color.White)
                    }

                    if (index != 5) Spacer(modifier = Modifier.height(10.dp))
                }
            }


            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    val navController = rememberNavController()
    Profile(navController)
}
