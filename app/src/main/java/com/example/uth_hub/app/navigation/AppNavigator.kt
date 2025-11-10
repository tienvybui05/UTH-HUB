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
import com.example.uth_hub.feature.auth.ui.*
import com.example.uth_hub.feature.notifications.ui.NotificationsScreen
import com.example.uth_hub.feature.post.ui.*
import com.example.uth_hub.feature.profile.ui.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.uth_hub.feature.auth.AuthConst
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
    val auth = remember { FirebaseAuth.getInstance() }
    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { fb ->
            isLoggedIn = fb.currentUser != null
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    // nhóm route có BottomBar và nhóm auth
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

    // chỉ hiện BottomBar nếu đã đăng nhập và không nằm ở auth/*
    val showBottomBar =
        isLoggedIn &&
                currentRoute != null &&
                authRoutes.none { pattern -> currentRoute.startsWith(pattern.substringBefore("/{")) } &&
                bottomBarRoutes.any { it == currentRoute }

    // ✅ LUÔN bắt đầu từ Splash để kiểm tra hồ sơ trước khi vào Home
    val startDest = AuthRoutes.Splash

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
                        val user = FirebaseAuth.getInstance().currentUser
                        val db = FirebaseFirestore.getInstance()

                        if (user == null) {
                            // 🔹 chưa đăng nhập → SignIn
                            navController.navigate(AuthRoutes.SignIn) { popUpTo(0) }
                        } else {
                            // 🔹 có user → kiểm tra document Firestore
                            db.collection(AuthConst.USERS)
                                .document(user.uid)
                                .get()
                                .addOnSuccessListener { doc ->
                                    if (!doc.exists() || doc.getString("mssv").isNullOrEmpty()) {
                                        // chưa hoàn tất hồ sơ → sang CompleteProfile
                                        val e = URLEncoder.encode(
                                            user.email,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("${AuthRoutes.CompleteProfile}/$e") {
                                            popUpTo(0)
                                        }
                                    } else {
                                        // đã có hồ sơ → vào Home
                                        navController.navigate(Routes.HomeScreen) {
                                            popUpTo(0)
                                        }
                                    }
                                }
                        }
                    }
                )
            }

            composable(AuthRoutes.SignIn) {
                SignInScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HomeScreen) {
                            popUpTo(0)
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
                CompleteProfileScreen(
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
            composable(Routes.Profile) { Profile(navController) }

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
