package com.example.uth_hub.feature.profile.ui.components

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareProfileSheet(
    profileUrl: String,
    usernameOrMssv: String,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    // QR bitmap được generate 1 lần theo profileUrl
    val qrBitmap by remember(profileUrl) {
        mutableStateOf(generateQrCode(profileUrl))
    }

    // Launcher share system
    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { /* không cần xử lý result */ }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color(0xFF008689),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hàng trên cùng: nút đóng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Đóng",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Chia sẻ trang cá nhân",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // chừa chỗ cho cân bằng layout
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))

            // Khung QR
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(Color.White, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = "QR trang cá nhân",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )
                } else {
                    Text("Không tạo được QR", color = Color.Red)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "@$usernameOrMssv",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(24.dp))

            // Nút: Sao chép liên kết
            FilledTonalButton(
                onClick = {
                    clipboard.setText(AnnotatedString(profileUrl))
                    Toast.makeText(context, "Đã sao chép liên kết", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sao chép liên kết",
                        color = Color(0xFF008689),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        tint = Color(0xFF008689)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Nút: Chia sẻ lên (share sheet của hệ thống)
            FilledTonalButton(
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, profileUrl)
                    }
                    shareLauncher.launch(
                        android.content.Intent.createChooser(intent, "Chia sẻ trang cá nhân")
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Chia sẻ lên",
                        color = Color(0xFF008689),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Outlined.IosShare,
                        contentDescription = null,
                        tint = Color(0xFF008689)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

/**
 * Generate QR bitmap từ 1 chuỗi link.
 * Trả về null nếu có lỗi.
 */
fun generateQrCode(content: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bmp.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE
                )
            }
        }
        bmp
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
