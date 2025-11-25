package com.example.uth_hub.feature.post.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.core.design.theme.ColorCustom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCommentTopBar(
    authorName: String,
    onBack: () -> Unit
) {
    Surface(color = Color.White, shadowElevation = 3.dp) {
        CenterAlignedTopAppBar(
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ColorCustom.primary
                    )
                }
            },
            title = {
                Text(
                    text = "Bài viết của $authorName",
                    fontSize = 18.sp,
                    color = ColorCustom.primary
                )
            }
        )
    }
}
