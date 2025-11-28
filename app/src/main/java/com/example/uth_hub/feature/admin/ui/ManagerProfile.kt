package com.example.uth_hub.feature.admin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.uth_hub.R
import com.example.uth_hub.app.navigation.AuthRoutes
import com.example.uth_hub.app.navigation.Routes
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.profile.ui.components.ChangeAvatarSheet
import com.example.uth_hub.feature.profile.ui.components.SettingsSheet
import com.example.uth_hub.feature.profile.util.rememberAvatarPicker
import com.example.uth_hub.feature.profile.viewmodel.ProfileViewModel
import com.example.uth_hub.feature.post.di.PostDI
import com.example.uth_hub.feature.post.viewmodel.FeedViewModel
import com.example.uth_hub.feature.post.viewmodel.FeedViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ExclamationCircle
import compose.icons.fontawesomeicons.solid.Ghost
import compose.icons.fontawesomeicons.solid.List

class Statistical(
    var quantity: Int,
    var title: String,
    var icon: ImageVector,
    var colorIcon: Color
)

@Composable
fun ManagerProfile(
    navController: NavController,
    profileVm: ProfileViewModel = viewModel(),
    feedVm: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            PostDI.providePostRepository(),
            PostDI.auth
        )
    )
) {
    var showSettings by remember { mutableStateOf(false) }
    var showChangeAvatar by remember { mutableStateOf(false) }

    val ui = profileVm.ui.collectAsState().value
    val user = ui.user

    val posts by feedVm.posts.collectAsState() // Lấy danh sách bài viết

    // Avatar Picker
    val avatarPicker = rememberAvatarPicker(
        onGalleryImagePicked = { uri -> uri?.let { profileVm.updateAvatarFromUri(it) } },
        onCameraImageTaken = { bitmap -> bitmap?.let { profileVm.updateAvatarFromBitmap(it) } }
    )

    // Thống kê: Số bài viết thật sự, số tố cáo tạm 0
    val listStatistical = listOf(
        Statistical(posts.size, "Bài viết", FontAwesomeIcons.Solid.Ghost, ColorCustom.primary),
        Statistical(0, "Tố cáo", FontAwesomeIcons.Solid.ExclamationCircle, Color.Red)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ===== HEADER =====
        Box {
            // Background ảnh header
            Image(
                painter = painterResource(id = R.drawable.nenprofile),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            // Menu icon
            IconButton(
                onClick = { showSettings = true },
                modifier = Modifier.padding(top=20.dp).align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.List,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Avatar + info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = user?.displayName ?: "—",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = user?.mssv ?: "—",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { showChangeAvatar = true }
                ) {
                    AsyncImage(
                        model = user?.photoUrl,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.avartardefault),
                        error = painterResource(R.drawable.avartardefault),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // ===== Card "Quản trị viên"  =====
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Card(
                    modifier = Modifier.padding(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x1A008689)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Quản trị viên",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorCustom.primary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
            }
        }

        // ===== SETTINGS SHEET =====
        if (showSettings) {
            SettingsSheet(
                onDismissRequest = { showSettings = false },
                onGoNotifications = null,
                onGoSaved = null,
                onGoLiked = null,
                onGoEditInfo = null,
                onGoChangeAvatar = {
                    showSettings = false
                    showChangeAvatar = true
                },
                onGoChangePw = {
                    showSettings = false
                    navController.navigate(Routes.ChangePassword)
                },
                onGoHelp = null,
                onGoTerms = null,
                onLogout = {
                    showSettings = false
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(AuthRoutes.SignIn) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }

        // ===== CHANGE AVATAR SHEET =====
        if (showChangeAvatar) {
            ChangeAvatarSheet(
                onPickFromGallery = { avatarPicker.openGallery() },
                onTakePhoto = { avatarPicker.openCamera() },
                onRemove = { profileVm.resetAvatarToGoogleDefault() },
                onDismiss = { showChangeAvatar = false }
            )
        }

        // ===== BODY =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp,30.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Thống kê
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x1A008689))
                    .padding(15.dp, 15.dp)
            ) {
                Text("Thống kê", modifier = Modifier.padding(bottom = 10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x1A008689))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listStatistical.forEach { item ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = item.colorIcon,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                )
                            }

                            Text(
                                "${item.quantity}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = item.colorIcon
                            )
                            Text(
                                item.title,
                                fontSize = 16.sp,
                                color = ColorCustom.secondText
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {  },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorCustom.primary)
                ) {
                    Text("Bài viết của tôi", fontSize = 16.sp)
                }

                Button(
                    onClick = { navController.navigate(Routes.PostManagement) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorCustom.primary)
                ) {
                    Text("Quản lý bài viết", fontSize = 16.sp)
                }
            }
        }
    }
}
