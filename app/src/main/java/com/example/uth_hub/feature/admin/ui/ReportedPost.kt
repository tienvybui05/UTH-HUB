package com.example.uth_hub.feature.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uth_hub.core.design.theme.ColorCustom
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ChevronLeft

@Composable
fun ReportedPost(navController: NavController){
    Column(modifier = Modifier.fillMaxSize().background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth().padding(5.dp,15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            TextButton(onClick = {

            }) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.ChevronLeft,
                    contentDescription = "Quay về",
                    tint = ColorCustom.primary,
                    modifier = Modifier.size(24.dp),
                )
                Text("Bài viết bị tố cáo ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ColorCustom.primary)
            }

            Row {
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = ColorCustom.primary)){}
        Button(onClick = {},
            modifier = Modifier.shadow(
                elevation =8.dp,
                shape = RoundedCornerShape(12.dp),
            ),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorCustom.primary
            )){
            Text(text = "--Tất cả khoa--", color = Color.White)
        }

    }
}
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Uth_hubTheme {
//        ReportedPost(rememberNavController())
//    }
//}