package com.example.uth_hub.Screens.auth.screen

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
import com.example.uth_hub.Screens.auth.component.*
import com.example.uth_hub.Screens.auth.presentation.SignInViewModel
import com.example.uth_hub.ui.theme.ColorCustom

@Composable
fun SignInScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    onForgotClick: () -> Unit,
    vm: SignInViewModel = viewModel()
) {
    // nền ảnh toàn màn
    Box(modifier = Modifier.fillMaxSize()) {
        AuthBackground()

        // banner trên cùng có chữ UTH HUB
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

        // phần thân: card (chồng lên banner) + nút login và link dưới card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(160.dp)) // đẩy xuống dưới banner

            // Card chồng lên 40dp
            Box(modifier = Modifier.offset(y = (-20).dp)) {
                AuthCard {
                    // heading to & căn giữa
                    Text(
                        text = "Welcome UTHers,\nlogin to start with us",
                        color = ColorCustom.secondText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    EmailField(email = vm.email.value, onValueChange = { vm.email.value = it })
                    Spacer(Modifier.height(10.dp))
                    PasswordField(password = vm.password.value, onValueChange = { vm.password.value = it })

                    // Forgot Password? căn giữa
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextLinkButton("Forgot Password?") { onForgotClick() }
                    }
                }
            }

            // Nút LOG IN ở ngoài card
            PrimaryButton(text = "LOG IN") {
                vm.onLoginClick(onLoginSuccess)
            }

            // Hàng SIGN UP dưới nút
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

/** Nền ảnh dùng cho tất cả màn auth */
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
private fun PreviewSignInScreen() {
    SignInScreen(onLoginSuccess = {}, onSignupClick = {}, onForgotClick = {})
}
