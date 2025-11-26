package com.example.uth_hub.feature.post.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.uth_hub.R
import com.example.uth_hub.core.design.components.DrawerMenu
import com.example.uth_hub.feature.post.ui.component.PostItem
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.core.design.theme.Uth_hubTheme
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.viewmodel.FeedViewModel
import com.example.uth_hub.feature.post.viewmodel.FeedViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.uth_hub.app.navigation.Routes
import com.example.uth_hub.feature.admin.ui.components.EmptyPostsState
import com.example.uth_hub.feature.admin.ui.components.LoadingSkeleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private val INSTITUTES = listOf(
    "Tất cả khoa",
    "Viện CNTT & Điện, điện tử",
    "Viện Cơ khí",
    "Viện Đường sắt tốc độ cao",
    "Viện Kinh tế & Phát triển Giao thông Vận tải",
    "Viện Hàng hải",
    "Viện Ngôn ngữ, Khoa học Chính trị & Xã hội",
    "Viện Nghiên cứu & Đào tạo Đèo Cả"
)

@Composable
fun HomeScreen(
    navController: NavController,
    vm: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            PostDI.providePostRepository(),
            PostDI.auth
        )
    )
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val posts by vm.posts.collectAsState()
    val isLoading by vm.isLoading.collectAsState() // ← Thêm trạng thái loading

    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    var avatarUrl by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }
    var selectedInstitute by remember { mutableStateOf("Tất cả khoa") }
    var expanded by remember { mutableStateOf(false) }

    val filteredPosts = remember(posts, selectedInstitute) {
        if (selectedInstitute == "Tất cả khoa") posts
        else posts.filter { it.authorInstitute == selectedInstitute }
    }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        val emailLocal = auth.currentUser?.email?.substringBefore("@") ?: "user"
        avatarUrl = auth.currentUser?.photoUrl?.toString() ?: ""
        handle = "@$emailLocal"

        if (uid != null) {
            try {
                val u = db.collection("users").document(uid).get().await()
                avatarUrl = (u.getString("photoUrl") ?: u.getString("avatarUrl") ?: avatarUrl).trim()
                handle = (u.getString("handle") ?: u.getString("styleName") ?: handle).trim()
                if (!handle.startsWith("@")) handle = "@$handle"
            } catch (_: Exception) { /* giữ fallback */ }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(
                onSettingsClick = { /* TODO */ },
                onHelpClick = { /* TODO */ }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().background(color = Color.White)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ColorCustom.primary, RoundedCornerShape(8.dp))
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
                    .background(color = Color.White)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("☰", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorCustom.primary)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.logouth),
                        contentDescription = "Logo Uth",
                        modifier = Modifier.weight(2f).height(40.dp)
                    )
                    Row(modifier = Modifier.weight(1f)) {}
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = if (avatarUrl.isNotBlank())
                            rememberAsyncImagePainter(avatarUrl)
                        else
                            rememberAsyncImagePainter(model = R.drawable.avartardefault),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Text(text = handle.ifBlank { "@user" }, fontSize = 16.sp, color = ColorCustom.secondText)
                        Text(
                            text = "Hôm nay có g hót ?",
                            fontSize = 13.sp,
                            color = Color(0xFF595959),
                            modifier = Modifier.clickable { /* điều hướng sang CreatePost nếu muốn */ }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=10.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp) // ← Giới hạn chiều rộng tối đa cho toàn bộ Box
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .border(1.dp, ColorCustom.primary, RoundedCornerShape(30.dp))
                            .clickable { expanded = true },
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedInstitute,
                                color = ColorCustom.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f) // ← Quan trọng: chiếm không gian còn lại
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Chọn khoa",
                                tint = ColorCustom.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(Color.White)
                            .fillMaxWidth(0.9f)
                    ) {
                        INSTITUTES.forEach { institute ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = institute,
                                        color = if (institute == selectedInstitute) ColorCustom.primary else Color.Black,
                                        fontWeight = if (institute == selectedInstitute) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    selectedInstitute = institute
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                // Hiển thị skeleton khi đang loading
                if (isLoading) {
                    LoadingSkeleton()
                } else if (filteredPosts.isEmpty()) {
                    // Chỉ hiển thị empty state khi đã load xong và thực sự không có bài viết
                    EmptyPostsState(selectedInstitute = selectedInstitute)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredPosts.size) { idx ->
                            val p = filteredPosts[idx]
                            PostItem(
                                postModel = p,
                                onLike = { vm.toggleLike(p.id) },
                                onComment = { navController.navigate("${Routes.PostComment}/${p.id}") },
                                onSave = { vm.toggleSave(p.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Uth_hubTheme {
        HomeScreen(rememberNavController())
    }
}