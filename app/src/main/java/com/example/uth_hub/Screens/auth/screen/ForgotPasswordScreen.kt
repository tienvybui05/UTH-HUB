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
import com.example.uth_hub.Screens.auth.presentation.ForgotPasswordViewModel
import com.example.uth_hub.ui.theme.ColorCustom

@Composable
fun ForgotPasswordScreen(
    onOtpSent: (email: String) -> Unit,
    vm: ForgotPasswordViewModel = viewModel()
) {
    Box(Modifier.fillMaxSize()) {
        AuthBackground()

        Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.TopCenter) {
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
                        text = "FORGET PASSWORD",
                        color = ColorCustom.secondText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(36.dp))
                    EmailField(email = vm.email.value, onValueChange = { vm.email.value = it })
                    Spacer(Modifier.height(46.dp))

                }
            }
            PrimaryButton(text = "SEND OTP") { vm.onSendOtpClick(onOtpSent) }
            vm.message.value?.let { msg -> Spacer(Modifier.height(8.dp)); Text(msg, color = ColorCustom.primaryText) }
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
private fun PreviewForgotPasswordScreen() {
    ForgotPasswordScreen(onOtpSent = {})
}
