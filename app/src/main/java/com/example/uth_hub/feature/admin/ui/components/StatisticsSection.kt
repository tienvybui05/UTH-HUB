package com.example.uth_hub.feature.admin.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.core.design.theme.ColorCustom

@Composable
fun StatisticsSection(
    filteredPostsCount: Int,
    selectedInstitute: String
) {
    Text(
        text = buildString {
            append("Hiển thị $filteredPostsCount bài viết")
            if (selectedInstitute != "Tất cả khoa") {
                append(" từ $selectedInstitute")
            }
        },
        fontSize = 14.sp,
        color = ColorCustom.secondText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
}