package com.example.uth_hub.Screens.auth.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uth_hub.R

@Composable
fun SocialRow(
    onGoogleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {},
    onAppleClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google",
            modifier = Modifier.size(40.dp).clickable { onGoogleClick() }
        )
        Image(
            painter = painterResource(id = R.drawable.ic_facebook),
            contentDescription = "Facebook",
            modifier = Modifier.size(40.dp).clickable { onFacebookClick() }
        )
        Image(
            painter = painterResource(id = R.drawable.ic_apple),
            contentDescription = "Apple",
            modifier = Modifier.size(40.dp).clickable { onAppleClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSocialRow() {
    Surface { SocialRow() }
}
