package com.example.uth_hub.feature.profile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeAvatarSheet(
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF005F61),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White) }
    ) {
        ChangeAvatarSheetContent(
            onPickFromGallery = { onPickFromGallery(); onDismiss() },
            onTakePhoto = { onTakePhoto(); onDismiss() },
            onRemove = { onRemove(); onDismiss() }
        )
    }
}

/* ---------------- Preview-friendly content ---------------- */

@Composable
private fun ChangeAvatarSheetContent(
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(horizontal = 20.dp)
    ) {

        SheetItem(
            icon = Icons.Outlined.Image,
            text = "Chọn ảnh từ thư viện",
            onClick = { onPickFromGallery() }
        )
        Spacer(Modifier.height(14.dp))

        SheetItem(
            icon = Icons.Outlined.CameraAlt,
            text = "Chụp ảnh",
            onClick = { onTakePhoto() }
        )
        Spacer(Modifier.height(14.dp))

        SheetItem(
            icon = Icons.Outlined.Delete,
            text = "Xóa",
            onClick = { onRemove() }
        )
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
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, color = Color.White)
    }
}

/* ---------------- WORKING PREVIEW ---------------- */

@Preview(showBackground = true, backgroundColor = 0xFF005F61)
@Composable
fun ChangeAvatarSheetPreview() {
    Surface(color = Color(0xFF005F61)) {
        ChangeAvatarSheetContent(
            onPickFromGallery = {},
            onTakePhoto = {},
            onRemove = {}
        )
    }
}
