package com.example.uth_hub.feature.admin.ui.components

// File: com/example/uth_hub/core/design/components/InstituteDropdown.kt


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.core.design.theme.ColorCustom

// Danh sách các viện/khoa dùng chung
val INSTITUTES = listOf(
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
fun InstituteDropdown(
    selectedInstitute: String,
    onInstituteSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isAdminStyle: Boolean = false,
    maxWidth: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .then(
                if (maxWidth) Modifier.fillMaxWidth()
                else Modifier.widthIn(max = 280.dp)
            )
    ) {
        // Surface với style khác nhau cho Admin và User
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(if (isAdminStyle) 12.dp else 30.dp))
                .clickable { expanded = true },
            color = if (isAdminStyle) ColorCustom.primary else Color.White,
            shape = RoundedCornerShape(if (isAdminStyle) 12.dp else 30.dp)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = if (isAdminStyle) 14.dp else 12.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedInstitute,
                    color = if (isAdminStyle) Color.White else ColorCustom.primary,
                    fontSize = if (isAdminStyle) 14.sp else 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Chọn khoa",
                    tint = if (isAdminStyle) Color.White else ColorCustom.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Dropdown Menu
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
                        onInstituteSelected(institute)
                        expanded = false
                    }
                )
            }
        }
    }
}