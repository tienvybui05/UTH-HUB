package com.example.uth_hub.feature.post.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uth_hub.core.design.components.PostItem
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.viewmodel.SavedPostsViewModel
import com.example.uth_hub.feature.post.viewmodel.SavedPostsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavePostScreen(navController: NavController) {
    val vm: SavedPostsViewModel = viewModel(
        factory = SavedPostsViewModelFactory(PostDI.providePostRepository())
    )
    val posts by vm.posts.collectAsState()

    Scaffold(
        topBar = {
            Surface(color = Color.White, shadowElevation = 3.dp) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF00796B))
                        }
                    },
                    title = {
                        Text("Bài viết đã lưu", fontSize = 18.sp, color = Color(0xFF00796B))
                    }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = padding.calculateTopPadding() + 8.dp, bottom = 16.dp)
        ) {
            items(posts.size) { i ->
                val p = posts[i]
                PostItem (
                    postModel = p,
                    onLike = { vm.toggleLike(p.id) },
                    onComment = { /* TODO: điều hướng comment */ },
                    onSave = { vm.toggleSave(p.id) }
                )
            }
        }
    }
}
