package com.example.uth_hub.Screens.auth.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.example.uth_hub.ui.theme.ColorCustom

@Composable
fun TextLinkButton(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = ColorCustom.primary,
        fontWeight = FontWeight.Medium,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTextLinkButton() {
    TextLinkButton(text = "SIGN UP", onClick = {})
}
