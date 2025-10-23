package com.example.uth_hub.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uth_hub.Screens.*
import com.example.uth_hub.Screens.Manager.ManagerProfile
import com.example.uth_hub.Screens.Manager.ManagerStudent
import com.example.uth_hub.Screens.Manager.PostManagement
import com.example.uth_hub.Screens.Manager.ReportedPost
import com.example.uth_hub.Screens.Profile.Profile
import com.example.uth_hub.Screens.auth.screen.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object AuthRoutes {
    const val Splash = "auth/splash"
    const val SignIn = "auth/signin"
    const val SignUp = "auth/signup"
    const val OtpSignup = "auth/otp_signup"          // + /{email}
    const val Forgot = "auth/forgot"
    const val OtpReset = "auth/otp_reset"            // + /{email}
    const val Reset = "auth/reset"                   // + /{email}
}

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        // nếu muốn vào thẳng Home thì đổi lại Screen.HomeScreen
        startDestination = AuthRoutes.Splash
    ) {
        // ========= AUTH =========
        composable(AuthRoutes.Splash) {
            SplashScreen(
                onFinish = { navController.navigate(AuthRoutes.SignIn) { popUpTo(AuthRoutes.Splash) { inclusive = true } } }
            )
        }
        composable(AuthRoutes.SignIn) {
            SignInScreen(
                onLoginSuccess = { navController.navigate(Screen.HomeScreen) { popUpTo(AuthRoutes.SignIn) { inclusive = true } } },
                onSignupClick = { navController.navigate(AuthRoutes.SignUp) },
                onForgotClick = { navController.navigate(AuthRoutes.Forgot) }
            )
        }
        composable(AuthRoutes.SignUp) {
            SignUpScreen(
                onSendOtp = { email ->
                    val e = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
                    navController.navigate("${AuthRoutes.OtpSignup}/$e")
                },
                onSignInClick = { navController.popBackStack() }
            )
        }
        composable("${AuthRoutes.OtpSignup}/{email}") { backStack ->
            val email = backStack.arguments?.getString("email") ?: ""
            OtpSignupScreen(
                email = email,
                onVerified = { navController.navigate(AuthRoutes.SignIn) { popUpTo(AuthRoutes.SignIn) { inclusive = true } } }
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

        // ========= APP (giữ nguyên của bạn) =========
        composable(Screen.HomeScreen) { HomeScreen(navController = navController) }
        composable(Screen.CreatePost) { CreatePost(navController = navController) }
        composable(Screen.Notification) { NotificationsScreen(navController = navController) }
        composable(Screen.Profile) { Profile(navController = navController) }
        composable(Screen.PostManagement) { PostManagement(navController = navController) }
        composable(Screen.ManagerProfile) { ManagerProfile(navController = navController) }
        composable(Screen.ManagerStudent) { ManagerStudent(navController = navController) }
        composable(Screen.ReportedPost) { ReportedPost(navController = navController) }
    }
}
