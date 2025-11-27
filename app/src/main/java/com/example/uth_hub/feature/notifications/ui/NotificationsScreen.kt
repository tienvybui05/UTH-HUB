package com.example.uth_hub.feature.notifications.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uth_hub.R
import com.example.uth_hub.feature.notifications.viewmodel.NotificationViewModel
import com.example.uth_hub.feature.notifications.viewmodel.NotificationViewModelFactory
import com.example.uth_hub.feature.notifications.data.NotificationRepository
import com.example.uth_hub.feature.notifications.model.NotificationModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {

    val vm: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(NotificationRepository())
    )

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val notifications by vm.notifications.collectAsState(initial = emptyList())


    LaunchedEffect(Unit) {
        vm.load(uid)
    }

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF00796B)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Thông báo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF00796B)
                        )
                    }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(notifications) { noti ->
                NotificationRow(noti)
            }
        }
    }
}

@Composable
fun NotificationRow(noti: NotificationModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color(0xFF00796B).copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.avartardefault),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(timeAgo(noti.timestamp), fontSize = 12.sp, color = Color.Gray)
            Text(noti.message, fontSize = 14.sp)
        }

        IconButton(onClick = { /* TODO delete */ }) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
        }
    }
}

fun timeAgo(time: Long): String {
    if (time == 0L) return ""
    val diff = System.currentTimeMillis() - time
    val min = diff / 60000
    if (min < 1) return "Vừa xong"
    if (min < 60) return "$min phút trước"
    val hour = min / 60
    if (hour < 24) return "$hour giờ trước"
    val day = hour / 24
    return "$day ngày trước"
}
