package com.example.uth_hub.Screens.auth.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun OtpBoxes(
    otpLength: Int = 4,
    onFilled: (String) -> Unit = {}
) {
    var otp by remember { mutableStateOf(List(otpLength) { "" }) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        otp.forEachIndexed { index, value ->
            OutlinedTextField(
                value = value,
                onValueChange = {
                    if (it.length <= 1 && it.all(Char::isDigit)) {
                        otp = otp.toMutableList().also { list -> list[index] = it }
                        val current = otp.joinToString("")
                        if (current.length == otpLength) onFilled(current)
                    }
                },
                modifier = Modifier
                    .width(58.dp)
                    .padding(4.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("") }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOtpBoxes() {
    OtpBoxes()
}
