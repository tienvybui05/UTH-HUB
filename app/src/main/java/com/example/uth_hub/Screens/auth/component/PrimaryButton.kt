package com.example.uth_hub.Screens.auth.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uth_hub.ui.theme.ColorCustom

@Composable
fun PrimaryButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(40), // bo tròn hẳn kiểu pill
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .width(240.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorCustom.primary)
        ) {
            Text(text = text, color = ColorCustom.primarybackground)
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewPrimaryButton() {
    PrimaryButton("LOG IN", onClick = {})
}
