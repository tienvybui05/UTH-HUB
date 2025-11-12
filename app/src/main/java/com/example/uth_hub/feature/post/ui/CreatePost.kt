
package com.example.uth_hub.feature.post.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.uth_hub.R
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.viewmodel.CreatePostViewModel
import com.example.uth_hub.feature.post.viewmodel.CreatePostViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePost(
    navController: NavController,
    vm: CreatePostViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = CreatePostViewModelFactory(PostDI.providePostRepository())
    )
) {
    var postContent by remember { mutableStateOf("") }
    val pickedUris = remember { mutableStateListOf<Uri>() }
    val ui by vm.ui.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    // --- Load profile hiện tại để lấy 4 biến author* ---
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    var authorName by remember { mutableStateOf("") }
    var authorHandle by remember { mutableStateOf("") }
    var authorInstitute by remember { mutableStateOf("") }
    var authorAvatarUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        val emailLocal = auth.currentUser?.email?.substringBefore('@') ?: "user"
        // fallback nhanh từ FirebaseAuth
        authorName = auth.currentUser?.displayName ?: ""
        authorHandle = if (authorName.isNotBlank()) "@$emailLocal" else "@$emailLocal"
        authorAvatarUrl = auth.currentUser?.photoUrl?.toString() ?: ""
        authorInstitute = ""

        if (uid != null) {
            try {
                val doc = db.collection("users").document(uid).get().await()
                // map linh hoạt các key phổ biến trong project UTH-Hub
                authorName = (doc.getString("displayName")
                    ?: doc.getString("name")
                    ?: authorName).trim()
                authorHandle = (doc.getString("handle")
                    ?: doc.getString("styleName") // nhiều nơi bạn dùng styleName
                    ?: "@$emailLocal").trim()
                authorInstitute = (doc.getString("institute") ?: authorInstitute).trim()
                authorAvatarUrl = (doc.getString("photoUrl")
                    ?: doc.getString("avatarUrl")
                    ?: authorAvatarUrl).trim()
            } catch (_: Exception) { /* giữ fallback, không crash UI */ }
        }
    }
    // ---------------------------------------------------

    // Photo Picker
    val pickImages = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 9)
    ) { uris ->
        pickedUris.clear()
        pickedUris.addAll(uris)
    }

    Scaffold(
        topBar = {
            Surface(color = Color.White, shadowElevation = 5.dp) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF00796B))
                        }
                    },
                    title = {
                        Text("Tạo bài viết", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF00796B))
                    },
                    actions = {
                        val canPost = (postContent.isNotBlank() || pickedUris.isNotEmpty()) && ui.posting.not()
                        TextButton(
                            enabled = canPost,
                            onClick = {
                                vm.create(
                                    content = postContent.trim(),
                                    images = pickedUris.toList(),
                                    authorName = authorName.ifBlank { "User" },
                                    authorHandle = authorHandle.ifBlank { "@user" },
                                    authorInstitute = authorInstitute,
                                    authorAvatarUrl = authorAvatarUrl
                                )
                            }
                        ) {
                            Text("Đăng", fontSize = 18.sp,
                                color = if (canPost) Color(0xFF00796B) else Color.Gray,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
        ) {
            // Header user
            Row(verticalAlignment = Alignment.CenterVertically) {
                val avatarPainter =
                    if (authorAvatarUrl.isNotBlank()) rememberAsyncImagePainter(authorAvatarUrl)
                    else rememberAsyncImagePainter(model = R.drawable.avartardefault)

                Image(
                    painter = avatarPainter,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(50.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(authorHandle.ifBlank { "@user" }, fontSize = 16.sp)
                    Text(
                        authorInstitute.ifBlank { "Viện Công nghệ thông tin và Điện, điện tử" },
                        fontSize = 13.sp, color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Icon chọn ảnh
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = {
                    pickImages.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Icon(Icons.Outlined.Image, contentDescription = "Chọn ảnh",
                        tint = Color(0xFF00796B), modifier = Modifier.size(28.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Nội dung
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

            // Grid preview ảnh
            if (pickedUris.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)
                ) {
                    items(pickedUris) { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(model = uri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }

    // Kết quả đăng
    when {
        ui.postedId != null -> {
            LaunchedEffect(ui.postedId) {
                postContent = ""; pickedUris.clear(); navController.popBackStack()
            }
        }
        ui.error != null -> {
            LaunchedEffect(ui.error) {
                scope.launch { snackbar.showSnackbar("Lỗi: ${ui.error}") }
            }
        }
        ui.posting -> {
            LaunchedEffect("posting") {
                scope.launch { snackbar.showSnackbar("Đang đăng...") }
            }
        }
    }
}
