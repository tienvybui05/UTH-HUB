package com.example.uth_hub.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uth_hub.Navigation.Screen.PostManagement
import com.example.uth_hub.Screens.CreatePost
import com.example.uth_hub.Screens.HomeScreen
import com.example.uth_hub.Screens.Manager.ManagerProfile
import com.example.uth_hub.Screens.Manager.ManagerStudent
import com.example.uth_hub.Screens.Manager.PostManagement
import com.example.uth_hub.Screens.Manager.ReportedPost
import com.example.uth_hub.Screens.Notification
import com.example.uth_hub.Screens.Profile.Profile

@Composable
fun NavGraph(navController : NavHostController,modifier: Modifier = Modifier){
    NavHost(navController = navController, startDestination = Screen.HomeScreen){

        composable(Screen.HomeScreen) {
            HomeScreen(navController = navController)
        }
        composable(Screen.CreatePost) {
            CreatePost(navController =navController)
        }
        composable(Screen.Notification) {
            Notification(navController = navController)
        }
        composable(Screen.Profile) {
            Profile(navController = navController)
        }
        composable(Screen.PostManagement) {
            PostManagement(navController = navController)
        }
        composable(Screen.ManagerProfile) {
            ManagerProfile(navController = navController)
        }
        composable(Screen.ManagerStudent) {
            ManagerStudent(navController = navController)
        }

        composable(Screen.ReportedPost) {
            ReportedPost(navController = navController)
        }
    }

}