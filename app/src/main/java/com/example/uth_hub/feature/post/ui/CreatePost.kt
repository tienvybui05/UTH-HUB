package com.example.uth_hub.feature.post.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.R
import com.example.uth_hub.feature.post.viewmodel.CreatePostViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePost(
    navController: NavController,
    vm: CreatePostViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = /* provide your VM factory that injects PostRepository */
    )
) {
    var postContent by remember { mutableStateOf("") }
    val pickedUris = remember { mutableStateListOf<Uri>() }
    val ui = vm.ui.collectAsState()

    // Photo picker
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        pickedUris.clear()
        pickedUris.addAll(uris.take(9)) // giới hạn 9 ảnh
    }

    Scaffold(
        topBar = {
            Surface(color = Color.White, shadowElevation = 5.dp) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF00796B))
                        }
                    },
                    title = { Text("Tạo bài viết", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF00796B)) },
                    actions = {
                        TextButton(
                            enabled = postContent.isNotBlank() || pickedUris.isNotEmpty(),
                            onClick = {
                                // TODO: Lấy info từ ProfileScreen/Firestore (author)
                                vm.create(
                                    content = postContent,
                                    images = pickedUris.toList(),
                                    authorName = "Tên người dùng",           // thay bằng dữ liệu thực
                                    authorHandle = "@handle",                // thay bằng dữ liệu thực
                                    authorInstitute = "Viện CNTT & Đ-ĐT",    // thay bằng dữ liệu thực
                                    authorAvatarUrl = "https://..."          // thay bằng dữ liệu thực
                                )
                            }
                        ) {
                            Text("Đăng", fontSize = 18.sp, color = Color(0xFF00796B), fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {
            // ... phần avatar + handle của bạn giữ nguyên

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = { picker.launch("image/*") }) {
                    Icon(imageVector = Icons.Outlined.Image, contentDescription = "Add Image", tint = Color(0xFF00796B), modifier = Modifier.size(28.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = postContent,
                onValueChange = { postContent = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                decorationBox = { inner ->
                    if (postContent.isEmpty()) {
                        Text("Hôm nay có gì hot?", color = Color.Gray.copy(alpha = 0.6f), fontSize = 16.sp)
                    }
                    inner()
                }
            )

            // preview ảnh đã chọn (grid 3 cột gọn gàng)
            if (pickedUris.isNotEmpty()) {
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pickedUris.size) { idx ->
                        val uri = pickedUris[idx]
                        androidx.compose.foundation.Image(
                            painter = rememberAsyncImagePainter(model = uri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)).aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }

    // xử lý kết quả
    ui.value.postedId?.let {
        // clear và back về Home
        postContent = ""
        pickedUris.clear()
        navController.popBackStack()
    }
    ui.value.error?.let { err ->
        SnackbarHost(hostState = remember { SnackbarHostState() })
        // bạn có thể show SnackBar hoặc Dialog báo lỗi
    }
}

// ===== Preview trong Android Studio =====
@Preview(showBackground = true)
@Composable
fun PreviewCreatePost() {
    CreatePost(navController = rememberNavController())
}
