@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.uth_hub.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.auth.ui.component.AuthBackground
import com.example.uth_hub.feature.auth.ui.component.AuthCard
import com.example.uth_hub.feature.auth.ui.component.PrimaryButton
import com.example.uth_hub.feature.auth.ui.component.UthTextField
import com.example.uth_hub.feature.profile.ui.components.ChangeAvatarSheet
import com.example.uth_hub.feature.profile.util.rememberAvatarPicker
import com.example.uth_hub.feature.profile.viewmodel.ProfileViewModel

// Danh sách 7 viện
private val INSTITUTES = listOf(
    "Viện CNTT & Điện, điện tử",
    "Viện Cơ khí",
    "Viện Đường sắt tốc độ cao",
    "Viện Kinh tế & Phát triển Giao thông Vận tải",
    "Viện Hàng hải",
    "Viện Ngôn ngữ, Khoa học Chính trị & Xã hội",
    "Viện Nghiên cứu & Đào tạo Đèo Cả"
)

@Composable
fun EditProfileScreen(
    navController: androidx.navigation.NavController,
    vm: ProfileViewModel = viewModel()
) {
    val ui = vm.ui.collectAsState().value
    val user = ui.user ?: return

    var mssv by remember { mutableStateOf(user.mssv ?: "") }
    var phone by remember { mutableStateOf(user.phone ?: "") }
    var classCode by remember { mutableStateOf(user.classCode ?: "") }
    var institute by remember { mutableStateOf(user.institute ?: "") }
    var instituteExpanded by remember { mutableStateOf(false) }

    var showAvatarSheet by remember { mutableStateOf(false) }

    val avatarPicker = rememberAvatarPicker(
        onGalleryImagePicked = { uri -> uri?.let { vm.updateAvatarFromUri(it) } },
        onCameraImageTaken = { bmp -> bmp?.let { vm.updateAvatarFromBitmap(it) } }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->

        if (showAvatarSheet) {
            ChangeAvatarSheet(
                onPickFromGallery = { avatarPicker.openGallery() },
                onTakePhoto = { avatarPicker.openCamera() },
                onRemove = { vm.resetAvatarToGoogleDefault() },
                onDismiss = { showAvatarSheet = false }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            AuthBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 50.dp)
            ) {

                item { Spacer(Modifier.height(30.dp)) }

                // Avatar
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.photoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .clickable { showAvatarSheet = true },
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.height(30.dp))
                }

                item {
                    Box(Modifier.offset(y = (-10).dp)) {
                        AuthCard {

                            Text(
                                text = "Chỉnh sửa hồ sơ",
                                color = ColorCustom.secondText,
                                fontSize = 25.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // MSSV
                            UthTextField(
                                label = "Mã số sinh viên",
                                value = mssv,
                                onValueChange = { mssv = it }
                            )
                            Spacer(Modifier.height(6.dp))

                            // SĐT
                            UthTextField(
                                label = "Số điện thoại",
                                value = phone,
                                onValueChange = { phone = it }
                            )
                            Spacer(Modifier.height(6.dp))

                            // Viện (Dropdown)
                            ExposedDropdownMenuBox(
                                expanded = instituteExpanded,
                                onExpandedChange = { instituteExpanded = !instituteExpanded }
                            ) {
                                TextField(
                                    value = institute,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Viện") },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = instituteExpanded
                                        )
                                    }
                                )
                                ExposedDropdownMenu(
                                    expanded = instituteExpanded,
                                    onDismissRequest = { instituteExpanded = false }
                                ) {
                                    INSTITUTES.forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(item) },
                                            onClick = {
                                                institute = item
                                                instituteExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(6.dp))

                            // Lớp
                            UthTextField(
                                label = "Lớp",
                                value = classCode,
                                onValueChange = { classCode = it }
                            )
                            Spacer(Modifier.height(20.dp))

                            // 🔥 Nút Lưu dùng PrimaryButton
                            PrimaryButton(
                                text = "Lưu thay đổi",
                                enabled = true
                            ) {
                                vm.updateUserProfile(
                                    mssv = mssv,
                                    phone = phone,
                                    institute = institute,
                                    classCode = classCode
                                )
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    val navController = rememberNavController()

    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            AuthBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 50.dp)
            ) {

                item { Spacer(Modifier.height(140.dp)) }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }

                item {
                    Box(Modifier.offset(y = (-30).dp)) {
                        AuthCard {

                            Text(
                                text = "Chỉnh sửa hồ sơ",
                                color = ColorCustom.secondText,
                                fontSize = 25.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            UthTextField(
                                label = "Mã số sinh viên",
                                value = "052205000366",
                                onValueChange = {}
                            )
                            Spacer(Modifier.height(6.dp))

                            UthTextField(
                                label = "Số điện thoại",
                                value = "0329236155",
                                onValueChange = {}
                            )
                            Spacer(Modifier.height(6.dp))

                            UthTextField(
                                label = "Viện",
                                value = "Viện Nghiên cứu & Đào tạo Đèo Cả",
                                onValueChange = {}
                            )
                            Spacer(Modifier.height(6.dp))

                            UthTextField(
                                label = "Lớp",
                                value = "CN21002",
                                onValueChange = {}
                            )
                            Spacer(Modifier.height(20.dp))

                            PrimaryButton(
                                text = "Lưu thay đổi",
                                enabled = true,
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}
