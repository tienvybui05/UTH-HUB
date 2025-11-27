package com.example.uth_hub.feature.admin.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.post.domain.model.PostModel

@Composable
fun PostManagementContent(
    isLoading: Boolean,
    isDeleting: Boolean,
    selectedInstitute: String,
    filteredPosts: List<PostModel>,
    onInstituteSelected: (String) -> Unit,
    onReportedPostsClick: () -> Unit,
    onDeletePost: (String) -> Unit,
    onLike: (String) -> Unit,
    onComment: (String) -> Unit,
    onSave: (String) -> Unit,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Filter Section
            FilterSection(
                selectedInstitute = selectedInstitute,
                onInstituteSelected = onInstituteSelected,
                onReportedPostsClick = onReportedPostsClick
            )

            // Thống kê - chỉ hiển thị khi không loading
            if (!isLoading) {
                StatisticsSection(
                    filteredPostsCount = filteredPosts.size,
                    selectedInstitute = selectedInstitute
                )
            }

            // Posts List với loading state
            when {
                isLoading -> AdminLoadingSkeleton()
                filteredPosts.isEmpty() -> EmptyPostsState(selectedInstitute = selectedInstitute)
                else -> PostsListSection(
                    filteredPosts = filteredPosts,
                    isDeleting = isDeleting,
                    onDeletePost = onDeletePost,
                    onViewReports = { /* TODO */ },
                    onLike = onLike,
                    onComment = onComment,
                    onSave = onSave,
                    navController = navController
                )
            }
        }

        // Loading indicator cho xóa bài - ĐẶT TRONG BOX SCOPE
        if (isDeleting) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = ColorCustom.primary
            )
        }
    }
}