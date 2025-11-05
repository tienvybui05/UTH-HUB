package com.example.uth_hub.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.example.uth_hub.feature.profile.ui.Profile
import com.example.uth_hub.feature.notifications.ui.NotificationsScreen
import com.example.uth_hub.feature.post.ui.CreatePost
import com.example.uth_hub.feature.post.ui.HomeScreen
import com.example.uth_hub.feature.post.ui.LikedPostScreen
import com.example.uth_hub.feature.post.ui.SavePostScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AuthRoutes {
    const val Splash = "auth/splash"
    const val SignIn = "auth/signin"
    const val SignUp = "auth/signup"

    // Forgot/Reset cho flow quên mật khẩu
    const val Forgot = "auth/forgot"
    const val OtpReset = "auth/otp_reset"            // + /{email}
    const val Reset = "auth/reset"                   // + /{email}

    // SignUp → CompleteProfile
    const val CompleteProfile = "auth/complete_profile" // + /{email}
}

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = AuthRoutes.Splash
    ) {
        // ========= AUTH =========
        composable(AuthRoutes.Splash) {
            SplashScreen(
                onFinish = {
                    navController.navigate(AuthRoutes.SignIn) {
                        popUpTo(AuthRoutes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(AuthRoutes.SignIn) {
            SignInScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HomeScreen) {
                        popUpTo(AuthRoutes.SignIn) { inclusive = true }
                    }
                },
                onSignupClick = { navController.navigate(AuthRoutes.SignUp) },
                onForgotClick = { navController.navigate(AuthRoutes.Forgot) },

                // User đăng nhập bằng Google lần đầu → sang CompleteProfile
                onNewUserFromGoogle = { email ->
                    val e = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                    navController.navigate("${AuthRoutes.CompleteProfile}/$e")
                }
            )
        }

        //  SignUp: chỉ Google → CompleteProfile / quay về SignIn
        composable(AuthRoutes.SignUp) {
            SignUpScreen(
                onGoToCompleteProfile = { email ->
                    val e = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                    navController.navigate("${AuthRoutes.CompleteProfile}/$e")
                },
                onGoToSignIn = { navController.popBackStack() }
            )
        }

        // Nhận email từ Google và hoàn tất hồ sơ
        composable("${AuthRoutes.CompleteProfile}/{email}") { backStack ->
            val encoded = backStack.arguments?.getString("email") ?: ""
            val email = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())

            com.example.uth_hub.feature.auth.ui.CompleteProfileScreen(
                emailDefault = email,
                onCompleted = {
                    // Vào Home và xoá toàn bộ stack Auth (Splash, SignIn, CompleteProfile)
                    navController.navigate(Routes.HomeScreen) {
                        popUpTo(AuthRoutes.Splash) { inclusive = true }
                    }
                }
            )
        }

        // ( flow quên mật khẩu)
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
                onVerified = {
                    navController.navigate("${AuthRoutes.Reset}/$email")
                }
            )
        }
        composable("${AuthRoutes.Reset}/{email}") {
            ResetPasswordScreen(
                onResetDone = {
                    navController.navigate(AuthRoutes.SignIn) {
                        popUpTo(AuthRoutes.SignIn) { inclusive = true }
                    }
                }
            )
        }

        // ========= APP =========
        composable(Routes.HomeScreen) { HomeScreen(navController) }
        composable(Routes.CreatePost) { CreatePost(navController) }
        composable(Routes.Notification) { NotificationsScreen(navController) }
        composable(Routes.Profile) { Profile(navController) }
        composable(Routes.PostManagement) { PostManagement(navController) }
        composable(Routes.ManagerProfile) { ManagerProfile(navController) }
        composable(Routes.ManagerStudent) { ManagerStudent(navController) }
        composable(Routes.ReportedPost) { ReportedPost(navController) }

        composable(Routes.LikedPost) { LikedPostScreen(navController) }
        composable(Routes.SavedPost) { SavePostScreen(navController) }
    }
}
