package com.example.uth_hub.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileTabBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Bài đăng", "File phương tiện")
    val icons = listOf(Icons.Default.Article, Icons.Default.Folder)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF176569)) // nền xanh ngọc
            .padding(horizontal = 8.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            // 🔹 Custom indicator trắng tinh, dài bằng 1/2 tab
            indicator = { tabPositions ->
                val currentTab = tabPositions[selectedTabIndex]
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(currentTab)
                        .width(currentTab.width / 2)
                        .height(4.dp)
                        .offset(y = (-7).dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = title,
                                tint = if (selectedTabIndex == index) Color.White else Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) Color.White else Color.LightGray,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTabBar() {
    ProfileTabBar(
        selectedTabIndex = 0,
        onTabSelected = {}
    )
}
