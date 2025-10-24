package com.example.uth_hub.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.R
import com.example.uth_hub.ui.theme.ColorCustom
import androidx.compose.material3.Divider
import androidx.compose.ui.unit.dp
import com.example.uth_hub.Screens.Shared.Post


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavePostScreen(navController: NavController) {
    var postContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 3.dp // hiệu ứng phân tách tự nhiên
            ) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF00796B)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Bài viết đã lưu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF00796B)
                        )
                    }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Row {


            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 66.dp)
            ) {
                items(5) { item ->
                    Post()
                }
            }
        }

    }
}


// ===== Preview trong Android Studio =====
@Preview(showBackground = true)
@Composable
fun PreviewSavePostScreen() {
    SavePostScreen(navController = rememberNavController())
}
