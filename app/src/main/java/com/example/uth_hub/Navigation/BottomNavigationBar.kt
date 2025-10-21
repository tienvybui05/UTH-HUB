package com.example.uth_hub.Navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.uth_hub.ui.theme.ColorCustom
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bell
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.UserCircle

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(navController: NavController){
    val items = listOf(
        BottomNavItem(Screen.HomeScreen,"Trang chủ", FontAwesomeIcons.Solid.Home),
        BottomNavItem(Screen.CreatePost,"Thêm bài viết", FontAwesomeIcons.Solid.Plus),
        BottomNavItem(Screen.Notification,"Thông báo", FontAwesomeIcons.Solid.Bell),
        BottomNavItem(Screen.Profile,"Cá nhân", FontAwesomeIcons.Solid.UserCircle),
    )
    Surface(
        color = ColorCustom.navigationBar ,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        tonalElevation = 10.dp,
        border = BorderStroke(1.dp, ColorCustom.primary)
    ){
        NavigationBar(
            containerColor = Color.Transparent
        ) {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route
            items.forEach {
                    item -> NavigationBarItem(selected = currentRoute==item.route, onClick = {
                if(currentRoute!=item.route){
                    navController.navigate(item.route){
                        popUpTo(Screen.HomeScreen)
                        launchSingleTop =true
                    }
                }
            },
                icon ={ Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(28.dp),
                    tint = if (currentRoute == item.route) ColorCustom.primary  else ColorCustom.primary
                ) },)
            }
        }
    }



}