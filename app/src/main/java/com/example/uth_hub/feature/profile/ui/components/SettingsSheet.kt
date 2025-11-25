package com.example.uth_hub.feature.profile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    onDismissRequest: () -> Unit,
    onGoNotifications: (() -> Unit)? = null,
    onGoSaved: (() -> Unit)? = null,
    onGoLiked: (() -> Unit)? = null,
    onGoEditInfo: (() -> Unit)? = null,
    onGoChangeAvatar: (() -> Unit)? = null,   // mới: đổi avatar
    onGoChangePw: (() -> Unit)? = null,
    onGoHelp: (() -> Unit)? = null,
    onGoTerms: (() -> Unit)? = null,
    onLogout: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Cài đặt",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // --- Các item ---
            SheetItem(icon = Icons.Outlined.Notifications, text = "Thông báo") {
                onGoNotifications?.invoke()
            }
            SheetItem(icon = Icons.Outlined.BookmarkBorder, text = "Đã lưu") {
                onGoSaved?.invoke()
            }
            SheetItem(icon = Icons.Outlined.FavoriteBorder, text = "Đã thích") {
                onGoLiked?.invoke()
            }
            SheetItem(icon = Icons.Outlined.Edit, text = "Chỉnh sửa thông tin") {
                onGoEditInfo?.invoke()
            }

            // MỚI: Thay đổi ảnh đại diện
            SheetItem(icon = Icons.Outlined.AccountCircle, text = "Thay đổi ảnh đại diện") {
                onGoChangeAvatar?.invoke()
            }

            SheetItem(icon = Icons.Outlined.VpnKey, text = "Đổi mật khẩu") {
                onGoChangePw?.invoke()
            }
            SheetItem(icon = Icons.Outlined.HelpOutline, text = "Trợ giúp") {
                onGoHelp?.invoke()
            }
            SheetItem(icon = Icons.Outlined.Info, text = "Giới thiệu và điều khoản") {
                onGoTerms?.invoke()
            }

            Divider(Modifier.padding(top = 12.dp, bottom = 4.dp))

            // --- Đăng xuất ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { onLogout() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null,
                    tint = Color.Red
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Đăng xuất",
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SheetItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Text(text)
    }
}
