package com.example.uth_hub.Screens.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.Navigation.BottomNavigationBar
import com.example.uth_hub.Screens.Profile.components.ProfileHeader
import com.example.uth_hub.Screens.Profile.components.TopBarSimple
import com.example.uth_hub.Screens.Shared.Post
import com.example.uth_hub.Screens.Profile.components.ProfileBackground
import com.example.uth_hub.Screens.Profile.components.ProfileTabBar

@Composable
fun Profile(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopBarSimple(onBackClick = { /* TODO */ }, onMenuClick = { /* TODO */ }) },
        bottomBar = { BottomNavigationBar(navController) },
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            // Nền
            ProfileBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
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

                // Thanh tab
                item {
                    ProfileTabBar(
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it }
                    )
                }

                // Hiển thị nội dung theo tab
                when (selectedTabIndex) {
                    0 -> { // Tab Bài đăng
                        items(5) {
                            Post()
                        }
                    }

                    1 -> { // Tab File phương tiện
                        items(6) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(Color(0x22FFFFFF))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🖼 Ảnh / Video ${it + 1}",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    val navController = rememberNavController()
    Profile(navController)
}
