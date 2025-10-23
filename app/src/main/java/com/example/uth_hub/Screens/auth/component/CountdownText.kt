package com.example.uth_hub.Screens.auth.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uth_hub.ui.theme.ColorCustom
import kotlinx.coroutines.delay

@Composable
fun CountdownText(
    totalSeconds: Int = 120,
    onResend: () -> Unit = {}
) {
    var seconds by remember { mutableIntStateOf(totalSeconds) }

    LaunchedEffect(Unit) {
        while (seconds > 0) {
            delay(1000)
            seconds--
        }
    }

    Row {
        Text(
            text = String.format("%02d:%02d Sec", seconds / 60, seconds % 60),
            color = ColorCustom.secondText
        )
        Spacer(modifier = Modifier.width(26.dp))
        Text(
            text = "Gửi lại",
            color = ColorCustom.linkPink,
            textDecoration = TextDecoration.Underline
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCountdownText() {
    CountdownText()
}
