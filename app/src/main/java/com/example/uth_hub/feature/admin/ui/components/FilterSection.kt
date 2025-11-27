package com.example.uth_hub.feature.admin.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.app.navigation.Routes
import com.example.uth_hub.core.design.theme.ColorCustom

@Composable
fun FilterSection(
    selectedInstitute: String,
    onInstituteSelected: (String) -> Unit,
    onReportedPostsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dropdown chọn khoa
            InstituteDropdown(
                selectedInstitute = selectedInstitute,
                onInstituteSelected = onInstituteSelected,
                modifier = Modifier.weight(1f),
                isAdminStyle = true,
                maxWidth = true
            )

            // Nút bài viết bị tố cáo
            Button(
                onClick = onReportedPostsClick,
                modifier = Modifier,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorCustom.linkPink
                )
            ) {
                Text(
                    text = "Bài bị tố cáo",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}