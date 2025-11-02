package com.example.uth_hub.feature.auth.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.uth_hub.core.design.theme.ColorCustom

@Composable
fun UthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        isError = isError,
        trailingIcon = trailingIcon,
        supportingText = {
            if (supportingText != null) Text(supportingText, color = ColorCustom.linkPink)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewUthTextField() {
    UthTextField(label = "Email", value = "datvl099@ut.edu.vn", onValueChange = {})
}
