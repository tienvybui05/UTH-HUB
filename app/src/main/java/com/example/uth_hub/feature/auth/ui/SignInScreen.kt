package com.example.uth_hub.feature.auth.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.auth.ui.component.*
import com.example.uth_hub.feature.auth.viewmodel.SignInViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color


@Composable
fun SignInScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    onForgotClick: () -> Unit,
    onNewUserFromGoogle: (emailFromGoogle: String) -> Unit,
    vm: SignInViewModel = viewModel()
) {
    // Google launcher
    val context = LocalContext.current
    val googleClient = remember { vm.googleClient(context) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        vm.handleGoogleResult(
            data = res.data,
            context = context,
            onNewUser = {
                val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
                onNewUserFromGoogle(email)
            },
            onSuccess = onLoginSuccess
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuthBackground()

        // Banner trên
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "UTH HUB",
                color = ColorCustom.primarybackground,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 80.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(160.dp))

            Box(modifier = Modifier.offset(y = (-20).dp)) {
                AuthCard {
                    Text(
                        text = "Welcome UTHers,\nlogin to start with us",
                        color = ColorCustom.secondText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    // 🔹 Đăng nhập bằng MSSV hoặc Email trường
                    UthTextField(
                        label = "MSSV hoặc Email @ut.edu.vn",
                        value = vm.emailOrMssv.value,
                        onValueChange = { vm.emailOrMssv.value = it }
                    )
                    vm.idError.value?.let { err ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error, // đỏ theo theme
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(Modifier.height(10.dp))

                    PasswordField(
                        label = "Mật khẩu",
                        password = vm.password.value,
                        onValueChange = { vm.password.value = it }
                    )
                    vm.passError.value?.let { err ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // Đăng nhập bằng MSSV/Email + pass
            PrimaryButton(text = "LOG IN") {
                vm.onLoginClick(onLoginSuccess)
            }

            //  Google
            Spacer(Modifier.height(10.dp))
            PrimaryButton(text = "Continue with Google") {
                launcher.launch(googleClient.signInIntent)
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don't have an account? ")
                TextLinkButton("SIGN UP") { onSignupClick() }
            }

        }
    }
}

/* -------- Preview (UI only) -------- */

@Composable
private fun SignInScreenContent(
    idOrEmail: String,
    password: String,
    onIdOrEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onForgotClick: () -> Unit,
    onGoogleClick: () -> Unit,
    message: String?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AuthBackground()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "UTH HUB",
                color = ColorCustom.primarybackground,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 80.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(160.dp))

            Box(modifier = Modifier.offset(y = (-20).dp)) {
                AuthCard {
                    Text(
                        text = "Welcome UTHers,\nlogin to start with us",
                        color = ColorCustom.secondText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    UthTextField(
                        label = "MSSV hoặc Email @ut.edu.vn",
                        value = idOrEmail,
                        onValueChange = onIdOrEmailChange
                    )
                    Spacer(Modifier.height(10.dp))

                    PasswordField(
                        label = "Mật khẩu",
                        password = password,
                        onValueChange = onPasswordChange
                    )
                }
            }

            PrimaryButton(text = "LOG IN") { onLoginClick() }

            Spacer(Modifier.height(10.dp))
            PrimaryButton(text = "Continue with Google") { onGoogleClick() }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don't have an account? ")
                TextLinkButton("SIGN UP") { onSignupClick() }
            }

            message?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(msg, color = ColorCustom.primaryText)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    SignInScreenContent(
        idOrEmail = "051205011574",
        password = "12345678",
        onIdOrEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onSignupClick = {},
        onForgotClick = {},
        onGoogleClick = {},
        message = null
    )
}
