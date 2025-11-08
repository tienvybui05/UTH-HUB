package com.example.uth_hub.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.uth_hub.feature.admin.ui.ManagerProfile
import com.example.uth_hub.feature.admin.ui.ManagerStudent
import com.example.uth_hub.feature.admin.ui.PostManagement
import com.example.uth_hub.feature.admin.ui.ReportedPost
import com.example.uth_hub.feature.auth.ui.ForgotPasswordScreen
import com.example.uth_hub.feature.auth.ui.OtpResetScreen
import com.example.uth_hub.feature.auth.ui.ResetPasswordScreen
import com.example.uth_hub.feature.auth.ui.SignInScreen
import com.example.uth_hub.feature.auth.ui.SignUpScreen
import com.example.uth_hub.feature.auth.ui.SplashScreen
import com.example.uth_hub.feature.notifications.ui.NotificationsScreen
import com.example.uth_hub.feature.post.ui.CreatePost
import com.example.uth_hub.feature.post.ui.HomeScreen
import com.example.uth_hub.feature.post.ui.LikedPostScreen
import com.example.uth_hub.feature.post.ui.SavePostScreen
import com.example.uth_hub.feature.profile.ui.Profile
import com.google.firebase.auth.FirebaseAuth
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AuthRoutes {
    const val Splash = "auth/splash"
    const val SignIn = "auth/signin"
    const val SignUp = "auth/signup"
    const val Forgot = "auth/forgot"
    const val OtpReset = "auth/otp_reset"            // + /{email}
    const val Reset = "auth/reset"                   // + /{email}
    const val CompleteProfile = "auth/complete_profile" // + /{email}
}

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    // 1) Theo dõi trạng thái đăng nhập
    val auth = remember { FirebaseAuth.getInstance() }
    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { fb ->
            isLoggedIn = fb.currentUser != null
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    // 2) Các nhóm route
    val bottomBarRoutes = remember {
        setOf(Routes.HomeScreen, Routes.CreatePost, Routes.Notification, Routes.Profile)
    }
    val authRoutes = remember {
        setOf(
            AuthRoutes.Splash, AuthRoutes.SignIn, AuthRoutes.SignUp,
            AuthRoutes.Forgot, "${AuthRoutes.OtpReset}/{email}",
            "${AuthRoutes.Reset}/{email}", "${AuthRoutes.CompleteProfile}/{email}"
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // 3) Chỉ show BottomBar nếu đã đăng nhập và không đứng ở auth/*
    val showBottomBar =
        isLoggedIn &&
                currentRoute != null &&
                authRoutes.none { pattern -> currentRoute.startsWith(pattern.substringBefore("/{")) } &&
                bottomBarRoutes.any { it == currentRoute }

    // 4) Start destination động
    val startDest = if (isLoggedIn) Routes.HomeScreen else AuthRoutes.Splash

    Scaffold(
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = modifier.padding(innerPadding)
        ) {
            // ===== AUTH =====
            composable(AuthRoutes.Splash) {
                SplashScreen(
                    onFinish = {
                        // chưa đăng nhập → vào SignIn
                        navController.navigate(AuthRoutes.SignIn) {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable(AuthRoutes.SignIn) {
                SignInScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HomeScreen) {
                            popUpTo(0)   // xoá toàn bộ stack auth
                            launchSingleTop = true
                        }
                    },
                    onSignupClick = { navController.navigate(AuthRoutes.SignUp) },
                    onForgotClick = { navController.navigate(AuthRoutes.Forgot) },
                    onNewUserFromGoogle = { email ->
                        val e = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                        navController.navigate("${AuthRoutes.CompleteProfile}/$e")
                    }
                )
            }

            composable(AuthRoutes.SignUp) {
                SignUpScreen(
                    onGoToCompleteProfile = { email ->
                        val e = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                        navController.navigate("${AuthRoutes.CompleteProfile}/$e")
                    },
                    onGoToSignIn = { navController.popBackStack() }
                )
            }

            composable("${AuthRoutes.CompleteProfile}/{email}") { backStack ->
                val encoded = backStack.arguments?.getString("email") ?: ""
                val email = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
                com.example.uth_hub.feature.auth.ui.CompleteProfileScreen(
                    emailDefault = email,
                    onCompleted = {
                        navController.navigate(Routes.HomeScreen) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AuthRoutes.Forgot) {
                ForgotPasswordScreen(
                    onOtpSent = { email ->
                        val e = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                        navController.navigate("${AuthRoutes.OtpReset}/$e")
                    }
                )
            }

            composable("${AuthRoutes.OtpReset}/{email}") { backStack ->
                val email = backStack.arguments?.getString("email") ?: ""
                OtpResetScreen(
                    email = email,
                    onVerified = { navController.navigate("${AuthRoutes.Reset}/$email") }
                )
            }

            composable("${AuthRoutes.Reset}/{email}") {
                ResetPasswordScreen(
                    onResetDone = {
                        navController.navigate(AuthRoutes.SignIn) { popUpTo(0) }
                    }
                )
            }

            // ===== APP (có BottomBar) =====
            composable(Routes.HomeScreen) { HomeScreen(navController) }
            composable(Routes.CreatePost) { CreatePost(navController) }
            composable(Routes.Notification) { NotificationsScreen(navController) }
            composable(Routes.Profile) { Profile(navController) } // khi Profile gọi signOut → listener cập nhật isLoggedIn=false → BottomBar ẩn ngay

            // ===== APP (không BottomBar) =====
            composable(Routes.PostManagement) { PostManagement(navController) }
            composable(Routes.ManagerProfile) { ManagerProfile(navController) }
            composable(Routes.ManagerStudent) { ManagerStudent(navController) }
            composable(Routes.ReportedPost) { ReportedPost(navController) }
            composable(Routes.LikedPost) { LikedPostScreen(navController) }
            composable(Routes.SavedPost) { SavePostScreen(navController) }
        }
    }
}
