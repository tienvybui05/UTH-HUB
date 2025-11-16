package com.example.uth_hub.feature.profile.util

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberAvatarPicker(
    onGalleryImagePicked: (Uri?) -> Unit,
    onCameraImageTaken: (Bitmap?) -> Unit
): AvatarPickerLauncher {

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            onGalleryImagePicked(uri)
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            onCameraImageTaken(bitmap)
        }

    return remember {
        AvatarPickerLauncher(
            openGallery = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            openCamera = {
                // 🔹 TakePicturePreview có input type là Void? → phải truyền null
                cameraLauncher.launch(null)
            }
        )
    }
}

class AvatarPickerLauncher(
    val openGallery: () -> Unit,
    val openCamera: () -> Unit
)
