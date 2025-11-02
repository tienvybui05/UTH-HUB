package com.example.uth_hub.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uth_hub.R
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.feature.auth.ui.component.AuthCard
import com.example.uth_hub.feature.auth.ui.component.EmailField
import com.example.uth_hub.feature.auth.ui.component.PasswordField
import com.example.uth_hub.feature.auth.ui.component.PrimaryButton
import com.example.uth_hub.feature.auth.ui.component.SocialRow
import com.example.uth_hub.feature.auth.ui.component.TextLinkButton
import com.example.uth_hub.feature.auth.ui.component.UthTextField
import com.example.uth_hub.feature.auth.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(
    onSendOtp: (email: String) -> Unit,
    onSignInClick: () -> Unit,
    vm: SignUpViewModel = viewModel()
) {
    Box(Modifier.fillMaxSize()) {
        AuthBackground()

        // Header UTH HUB
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

        // Card
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(160.dp))
            Box(Modifier.offset(y = (-20).dp)) {
                AuthCard {
                    Text(
                        text = "Hello my new friend!",
                        color = ColorCustom.secondText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    UthTextField(label = "Họ và tên", value = vm.fullName.value, onValueChange = { vm.fullName.value = it })
                    Spacer(Modifier.height(2.dp))
                    UthTextField(label = "Số điện thoại", value = vm.phone.value, onValueChange = { vm.phone.value = it })
                    Spacer(Modifier.height(2.dp))
                    EmailField(email = vm.email.value, onValueChange = { vm.email.value = it })
                    Spacer(Modifier.height(2.dp))
                    PasswordField(label = "Mật khẩu", password = vm.password.value, onValueChange = { vm.password.value = it })
                    Spacer(Modifier.height(2.dp))
                    PasswordField(label = "Xác nhận mật khẩu", password = vm.confirmPassword.value, onValueChange = { vm.confirmPassword.value = it })
                }
            }
            PrimaryButton(text = "SIGN UP") { vm.onSignupClick(onSendOtp) }

            vm.message.value?.let { msg ->
                Spacer(Modifier.height(8.dp)); Text(msg, color = ColorCustom.primaryText)
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Already have an account? ")
                TextLinkButton("SIGN IN") { onSignInClick() }
            }
            Spacer(Modifier.height(16.dp))
            SocialRow()
        }
    }
}

@Composable
private fun AuthBackground() {
    Image(
        painter = painterResource(id = R.drawable.nenauth),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewSignUpScreen() {
    SignUpScreen(onSendOtp = {}, onSignInClick = {})
}
