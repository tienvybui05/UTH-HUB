package com.example.uth_hub.feature.auth.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uth_hub.core.design.theme.ColorCustom

@Composable
fun AuthCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(ColorCustom.primarybackground, RoundedCornerShape(16.dp))
            .padding(20.dp),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAuthCard() {
    AuthCard { Text("Card content", color = ColorCustom.primaryText) }
}
