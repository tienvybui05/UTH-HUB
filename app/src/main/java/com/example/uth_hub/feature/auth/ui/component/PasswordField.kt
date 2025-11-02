package com.example.uth_hub.feature.auth.ui.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PasswordField(
    label: String = "Mật khẩu",
    password: String,
    onValueChange: (String) -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    UthTextField(
        label = label,
        value = password,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordField() {
    var pass by remember { mutableStateOf("") }
    PasswordField(password = pass, onValueChange = { pass = it })
}
