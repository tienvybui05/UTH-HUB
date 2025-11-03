package com.example.uth_hub.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.core.design.theme.ColorCustom


@Composable
fun DrawerMenu(
    onSettingsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp) // Giới hạn chiều rộng
    ) {
        Column(modifier = Modifier.background(Color.White)) {
            Text(
                "Feed",
                fontSize = 22.sp,
                color = ColorCustom.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(15.dp)
            )
            HorizontalDivider()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(ColorCustom.primary)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                ) {
                    NavigationDrawerItem(
                        label = { Text("Cài đặt") },
                        selected = false,
                        onClick = onSettingsClick
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .background(ColorCustom.primary)
                    )
                    NavigationDrawerItem(
                        label = { Text("Trợ giúp") },
                        selected = false,
                        onClick = onHelpClick
                    )
                }
            }
        }
    }
}

