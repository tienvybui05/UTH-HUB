package com.example.uth_hub.core.design.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun Avartar(avartar:Int){
    Image(
        painter = painterResource(avartar),
        contentDescription = "ảnh chân dung",
        modifier = Modifier.size(50.dp).clip(shape = CircleShape)
    )

}