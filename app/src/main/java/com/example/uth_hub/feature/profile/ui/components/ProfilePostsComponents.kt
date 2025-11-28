package com.example.uth_hub.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.BoxWithConstraints

// =======================
//   DATA & STATE
// =======================

data class UserPost(
    val id: String,
    val content: String,
    val imageUrls: List<String>,
    val createdAt: Long = 0L
)

data class UserPostsState(
    val loading: Boolean = false,
    val posts: List<UserPost> = emptyList(),
    val error: String? = null
)

// =======================
//   FIRESTORE LOAD
// =======================

@Composable
fun rememberUserPosts(userId: String): State<UserPostsState> {
    val state = remember { mutableStateOf(UserPostsState(loading = true)) }

    LaunchedEffect(userId) {
        if (userId.isBlank()) {
            state.value = UserPostsState(loading = false, posts = emptyList(), error = null)
            return@LaunchedEffect
        }
        try {
            state.value = UserPostsState(loading = true)

            // Đọc toàn bộ posts, lọc theo authorId ở client (tránh index)
            val snap = FirebaseFirestore.getInstance()
                .collection("posts")
                .get()
                .await()

            val posts = snap.documents.mapNotNull { doc ->
                val authorId = doc.getString("authorId") ?: return@mapNotNull null
                if (authorId != userId) return@mapNotNull null

                val content = doc.getString("content") ?: ""
                val images = (doc.get("imageUrls") as? List<*>)?.mapNotNull { it as? String }
                    ?: emptyList()

                val createdAtMillis = when (val raw = doc.get("createdAt")) {
                    is Long -> raw
                    is Timestamp -> raw.toDate().time
                    else -> 0L
                }

                UserPost(
                    id = doc.id,
                    content = content,
                    imageUrls = images,
                    createdAt = createdAtMillis
                )
            }.sortedByDescending { it.createdAt }

            state.value = UserPostsState(
                loading = false,
                posts = posts,
                error = null
            )
        } catch (e: Exception) {
            state.value = UserPostsState(
                loading = false,
                posts = emptyList(),
                error = e.message
            )
        }
    }

    return state
}

// =======================
//   TAB "BÀI ĐĂNG"
// =======================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfilePostsTab(
    state: UserPostsState,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        when {
            state.loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Text(
                    text = "Lỗi tải bài viết: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.posts.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Chưa có bài đăng", color = Color.White)
                }
            }

            else -> {
                state.posts.forEach { post ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        if (post.content.isNotBlank()) {
                            Text(
                                text = post.content,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(6.dp))
                        }

                        if (post.imageUrls.isNotEmpty()) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                maxItemsInEachRow = 3
                            ) {
                                post.imageUrls.forEach { url ->
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clickable { onImageClick(url) }
                                    ) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.DarkGray, RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(Color(0x33FFFFFF))
                    )
                }
            }
        }
    }
}

// =======================
//   TAB "FILE PHƯƠNG TIỆN" – GRID 3 CỘT KÍN
// =======================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileMediaTab(
    state: UserPostsState,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.loading -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Lỗi tải ảnh/video: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        else -> {
            val mediaUrls = state.posts
                .flatMap { it.imageUrls }
                .distinct()

            if (mediaUrls.isEmpty()) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Chưa có ảnh/video", color = Color.White)
                }
            } else {
                BoxWithConstraints(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    val columns = 3
                    val spacing = 4.dp
                    val totalSpacing = spacing * (columns - 1)
                    val cellSize = (maxWidth - totalSpacing) / columns

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        verticalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        mediaUrls.forEach { url ->
                            Box(
                                modifier = Modifier
                                    .width(cellSize)
                                    .aspectRatio(1f) // ô vuông
                                    .clickable { onImageClick(url) }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.DarkGray, RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =======================
//   FULL SCREEN IMAGE
// =======================

@Composable
fun FullScreenImageDialog(
    imageUrl: String?,
    onDismiss: () -> Unit
) {
    if (imageUrl == null) return

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = Color.Black
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Nhấn để đóng",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}
