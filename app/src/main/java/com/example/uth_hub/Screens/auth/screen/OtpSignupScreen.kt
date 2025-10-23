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
import com.example.uth_hub.Screens.auth.presentation.OtpSignupViewModel
import com.example.uth_hub.ui.theme.ColorCustom

@Composable
fun OtpSignupScreen(
    email: String,
    onVerified: () -> Unit,
    onResend: () -> Unit = {},
    vm: OtpSignupViewModel = viewModel()
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
                        text = "OTP VERIFICATION",
                        color = ColorCustom.secondText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text= "Nhập mã OTP đã gửi tới – $email",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(22.dp))
                    OtpBoxes(onFilled = { vm.otp.value = it })
                    Spacer(Modifier.height(38.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CountdownText(onResend = onResend)
                    }
                }
            }
            PrimaryButton(text = "SUBMIT") { vm.onVerifyClick(onVerified) }
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
private fun PreviewOtpSignupScreen() {
    OtpSignupScreen(email = "sv@uth.edu.vn", onVerified = {})
}
