package com.example.uth_hub.app.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.uth_hub.core.design.theme.ColorCustom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bell
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.UserCircle
import kotlinx.coroutines.tasks.await

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var role by remember { mutableStateOf("student") }

    // ✅ Lấy role user hiện tại
    LaunchedEffect(auth.currentUser) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        val doc = db.collection("users").document(uid).get().await()
        role = doc.getString("role") ?: "student"
    }

    // ✅ Nếu là admin thì route Profile -> ManagerProfile
    val profileRoute = if (role == "admin") Routes.ManagerProfile else Routes.Profile

    val items = listOf(
        BottomNavItem(Routes.HomeScreen, "Trang chủ", FontAwesomeIcons.Solid.Home),
        BottomNavItem(Routes.CreatePost, "Bài viết", FontAwesomeIcons.Solid.Plus),
        BottomNavItem(Routes.Notification, "Thông báo", FontAwesomeIcons.Solid.Bell),
        BottomNavItem(profileRoute, "Cá nhân", FontAwesomeIcons.Solid.UserCircle),
    )

    Surface(
        color = ColorCustom.navigationBar,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        tonalElevation = 10.dp,
        border = BorderStroke(1.dp, ColorCustom.primary)
    ) {
        NavigationBar(containerColor = Color.Transparent) {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(Routes.HomeScreen)
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(20.dp),
                            tint = ColorCustom.primary
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            color = ColorCustom.primary
                        )
                    },
                    alwaysShowLabel = true
                )
            }
        }
    }
}

