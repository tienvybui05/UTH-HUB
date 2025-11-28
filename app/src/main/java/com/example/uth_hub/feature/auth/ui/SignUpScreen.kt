package com.example.uth_hub.feature.auth.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
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
import com.example.uth_hub.feature.auth.AuthConst
import com.example.uth_hub.feature.auth.ui.component.AuthBackground
import com.example.uth_hub.feature.auth.ui.component.AuthCard
import com.example.uth_hub.feature.auth.ui.component.PrimaryButton
import com.example.uth_hub.feature.auth.ui.component.TextLinkButton
import com.example.uth_hub.feature.auth.viewmodel.SignUpViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun SignUpScreen(
    onGoToCompleteProfile: (emailFromGoogle: String) -> Unit,
    onGoToSignIn: () -> Unit,
    vm: SignUpViewModel = viewModel()
) {
    val context = LocalContext.current
    val googleClient: GoogleSignInClient = remember { vm.googleClient(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        vm.handleGoogleResult(
            data = res.data,
            context = context,
            onNewUser = { email -> onGoToCompleteProfile(email) },
            onAlreadyHasAccount = onGoToSignIn
        )
    }

    Box(Modifier.fillMaxSize()) {
        AuthBackground()

        // Header
        Box(
            modifier = Modifier.fillMaxWidth().height(180.dp),
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

        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(160.dp))
            Box(Modifier.offset(y = (-20).dp)) {
                AuthCard {
                    Text(
                        text = "Đăng ký tài khoản",
                        color = ColorCustom.secondText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Chỉ chấp nhận email ${AuthConst.UTH_DOMAIN}",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    PrimaryButton(
                        text = if (vm.isLoading.value) "Đang xử lý..." else "Tiếp tục với Google",
                        enabled = !vm.isLoading.value
                    ) {
                        launcher.launch(googleClient.signInIntent)
                    }

                    vm.message.value?.let { msg ->
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Đã có tài khoản? ")
                        TextLinkButton("ĐĂNG NHẬP") { onGoToSignIn() }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewSignUpScreen() {
    SignUpScreen(
        onGoToCompleteProfile = {},
        onGoToSignIn = {}
    )
}
