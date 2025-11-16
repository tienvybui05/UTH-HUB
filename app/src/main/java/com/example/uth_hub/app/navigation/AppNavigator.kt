package com.example.uth_hub.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.uth_hub.feature.admin.ui.*
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    val bottomBarRoutes = remember {
        setOf(Routes.HomeScreen, Routes.CreatePost, Routes.Notification, Routes.Profile, Routes.ManagerProfile)
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

    val showBottomBar =
        isLoggedIn &&
                currentRoute != null &&
                authRoutes.none { pattern -> currentRoute.startsWith(pattern.substringBefore("/{")) } &&
                bottomBarRoutes.any { it == currentRoute }

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
                            navController.navigate(AuthRoutes.SignIn) { popUpTo(0) }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val doc = db.collection(AuthConst.USERS)
                                        .document(user.uid)
                                        .get()
                                        .await()

                                    if (!doc.exists() || doc.getString("mssv").isNullOrEmpty()) {
                                        val e = URLEncoder.encode(
                                            user.email,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("${AuthRoutes.CompleteProfile}/$e") {
                                            popUpTo(0)
                                        }
                                    } else {
                                        val role = doc.getString("role") ?: UserRole.STUDENT
                                        if (role == UserRole.ADMIN) {
                                            // ✅ admin vào trang quản lý
                                            navController.navigate(Routes.ManagerProfile) {
                                                popUpTo(0)
                                            }
                                        } else {
                                            // ✅ student vào home
                                            navController.navigate(Routes.HomeScreen) {
                                                popUpTo(0)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    navController.navigate(AuthRoutes.SignIn) { popUpTo(0) }
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


            composable("${AuthRoutes.Reset}/{email}") {
                ResetPasswordScreen(
                    onResetDone = {
                        navController.navigate(AuthRoutes.SignIn) { popUpTo(0) }
                    }
                )
            }

            // ===== APP =====
            composable(Routes.HomeScreen) { HomeScreen(navController) }
            composable(Routes.CreatePost) { CreatePost(navController) }
            composable(Routes.Notification) { NotificationsScreen(navController) }

            // ✅ tách rõ giữa student và admin khi vào trang cá nhân
            composable(Routes.Profile) {
                val user = FirebaseAuth.getInstance().currentUser
                val db = FirebaseFirestore.getInstance()
                var role by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(user) {
                    if (user != null) {
                        val doc = db.collection(AuthConst.USERS)
                            .document(user.uid)
                            .get()
                            .await()
                        role = doc.getString("role") ?: UserRole.STUDENT
                    }
                }

                when (role) {
                    UserRole.ADMIN -> ManagerProfile(navController)
                    UserRole.STUDENT -> Profile(navController)
                    else -> Profile(navController) // fallback
                }
            }

            // ✅ Màn đổi mật khẩu
            composable(Routes.ChangePassword) {
                ResetPasswordScreen(
                    onResetDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            // ===== ADMIN / OTHER =====
            composable(Routes.PostManagement) { PostManagement(navController) }
            composable(Routes.ManagerProfile) { ManagerProfile(navController) }
            composable(Routes.ManagerStudent) { ManagerStudent(navController) }
            composable(Routes.ReportedPost) { ReportedPost(navController) }
            composable(Routes.LikedPost) { LikedPostScreen(navController) }
            composable(Routes.SavedPost) { SavePostScreen(navController) }
        }
    }
}
