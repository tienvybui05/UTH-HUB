package com.example.uth_hub.feature.profile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
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
    onGoSaved:   (() -> Unit)? = null,
    onGoLiked:   (() -> Unit)? = null,
    onGoEditInfo:(() -> Unit)? = null,
    onGoChangePw:(() -> Unit)? = null,
    onGoHelp:    (() -> Unit)? = null,
    onGoTerms:   (() -> Unit)? = null,
    onLogout:    () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false // half ↔ full
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
        // ⬅ BỎ contentWindowInsets vì bản bạn dùng chưa có API này
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // chừa chỗ cho system bar
                .imePadding(),           // chừa chỗ cho keyboard
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item {
                Text(
                    "Cài đặt",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item { SheetItem(Icons.Outlined.Notifications, "Thông báo") { onGoNotifications?.invoke() } }
            item { SheetItem(Icons.Outlined.BookmarkBorder, "Đã lưu") { onGoSaved?.invoke() } }
            item { SheetItem(Icons.Outlined.FavoriteBorder, "Đã thích") { onGoLiked?.invoke() } }
            item { SheetItem(Icons.Outlined.Edit, "Chỉnh sửa thông tin") { onGoEditInfo?.invoke() } }
            item { SheetItem(Icons.Outlined.VpnKey, "Đổi mật khẩu") { onGoChangePw?.invoke() } }
            item { SheetItem(Icons.Outlined.HelpOutline, "Trợ giúp") { onGoHelp?.invoke() } }
            item { SheetItem(Icons.Outlined.Info, "Giới thiệu và điều khoản") { onGoTerms?.invoke() } }

            item { Divider(Modifier.padding(top = 12.dp, bottom = 4.dp)) }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable { onLogout() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Outlined.Logout, contentDescription = null, tint = Color.Red)
                    Spacer(Modifier.width(12.dp))
                    Text("Đăng xuất", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            }
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
