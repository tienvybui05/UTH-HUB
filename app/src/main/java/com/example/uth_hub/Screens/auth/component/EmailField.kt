package com.example.uth_hub.Screens.auth.component

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EmailField(
    email: String,
    onValueChange: (String) -> Unit,
    error: String? = null
) {
    UthTextField(
        label = "Email (dùng mail trường)",
        value = email,
        onValueChange = onValueChange,
        isError = error != null,
        supportingText = error
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEmailField() {
    var text by remember { mutableStateOf("") }
    EmailField(email = text, onValueChange = { text = it })
}
