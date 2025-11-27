package com.example.uth_hub.feature.admin.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.uth_hub.feature.admin.ui.AdminPostItem
import com.example.uth_hub.feature.post.domain.model.PostModel

@Composable
fun PostsListSection(
    filteredPosts: List<PostModel>,
    isDeleting: Boolean,
    onDeletePost: (String) -> Unit,
    onViewReports: (String) -> Unit,
    onLike: (PostModel) -> Unit,
    onComment: (String) -> Unit,
    onSave: (String) -> Unit,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filteredPosts) { post ->
            AdminPostItem(
                post = post,
                onDeletePost = onDeletePost,
                onViewReports = onViewReports,
                onLike = { onLike(post) },
                onComment = onComment,
                onSave = onSave,
                navController = navController,
                isLoading = isDeleting
            )
        }
    }
}