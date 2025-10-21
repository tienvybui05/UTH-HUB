package com.example.uth_hub.Screens.Manager

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.Screens.Shared.Avartar
import com.example.uth_hub.ui.theme.ColorCustom
import com.example.uth_hub.ui.theme.Uth_hubTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ChevronLeft
import compose.icons.fontawesomeicons.solid.Fingerprint
import compose.icons.fontawesomeicons.solid.Trash

@Composable
fun PostManagement(navController: NavController){
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
                Text("Bài viết ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ColorCustom.primary)
            }

            Row {
                Button(onClick = {},
                    modifier = Modifier,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB42A46)
                    )){
                        Text(text = "Bị tố cáo", color = Color.White)
                }
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
//        PostManagement(rememberNavController())
//    }
//}