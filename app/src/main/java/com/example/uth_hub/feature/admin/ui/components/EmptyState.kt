package com.example.uth_hub.feature.admin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.R
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String = "📝 Chưa có bài viết nào",
    subtitle: String = "Các bài viết mới sẽ xuất hiện ở đây",
    showSubtitle: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth().clip(shape = RoundedCornerShape(10.dp))
            .padding(15.dp).background(Color(0xFFE8FDFD)).padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id =R.drawable.boxempty ),
            contentDescription = "empty",
            modifier= Modifier.size(200.dp)
        )
        Text(
            text = title,
            fontSize = 16.sp,
            color = ColorCustom.secondText,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (showSubtitle) {
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Extension function cho trường hợp lọc theo khoa
@Composable
fun EmptyPostsState(
    selectedInstitute: String = "Tất cả khoa"
) {
    val title = if (selectedInstitute == "Tất cả khoa") {
        "📝 Chưa có bài viết nào"
    } else {
        "Không có bài viết nào từ $selectedInstitute"
    }

    val subtitle = "Các bài viết mới sẽ xuất hiện ở đây"

    EmptyState(
        title = title,
        subtitle = subtitle
    )
}