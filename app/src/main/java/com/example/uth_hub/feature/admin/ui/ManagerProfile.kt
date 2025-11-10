package com.example.uth_hub.feature.admin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.uth_hub.R
import com.example.uth_hub.app.navigation.Routes
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.core.design.theme.Uth_hubTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ExclamationCircle
import compose.icons.fontawesomeicons.solid.Ghost
import compose.icons.fontawesomeicons.solid.UserCircle

public class Statistical(
    var quantity:Int,
    var title:String,
    var icon :ImageVector,
    var colorIcon: Color
){}
@Composable
fun ManagerProfile(navController: NavController){

    var listStatistical = listOf(Statistical(100,"Bài viêt", FontAwesomeIcons.Solid.Ghost,
        ColorCustom.primary),
                                    Statistical(100,"Tố cáo", FontAwesomeIcons.Solid.ExclamationCircle,Color.Red),
                                    Statistical(100,"Sinh viên", FontAwesomeIcons.Solid.UserCircle,
                                        ColorCustom.primary))
    Column(modifier = Modifier.fillMaxSize().background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.nenprofile),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Row(modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart).padding(10.dp) ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Column() {
                    Text("Đinh Quốc Đạt", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, )
                    Text("052205000317" ,fontSize = 16.sp,  color = Color.White)
                }
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.avartardefault),
                    error = painterResource(R.drawable.avartardefault),
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
            Card(modifier = Modifier.align(Alignment.BottomCenter).clip(RoundedCornerShape(4.dp)).background(color = Color.White).padding(19.dp ,8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                ){
                Text("Quản trị viên " , fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorCustom.primary)
            }

        }

        Column(modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Column(modifier = Modifier.fillMaxWidth().shadow(
                elevation =8.dp,
                shape = RoundedCornerShape(12.dp),
            ).clip(shape = RoundedCornerShape(10.dp)).background(color = ColorCustom.secondBackground).padding(15.dp,10.dp),) {
                Text(" Thống kê")
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = Color(0xFF9F9F9F))){}
                Row(modifier = Modifier.fillMaxWidth().padding(top=5.dp).background(color = Color.Transparent).padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    listStatistical.forEach {
                            item -> Column(modifier = Modifier.weight(1f).clip(shape = RoundedCornerShape(8.dp)).background(color = Color.White).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ){
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Row {  }
                            Icon(
                                imageVector = item.icon,
                                contentDescription = "${item.title}",
                                modifier = Modifier.size(20.dp),
                                tint = item.colorIcon
                            )
                        }

                        Text(text = "${item.quantity}", modifier = Modifier, fontSize = 18.sp , fontWeight = FontWeight.Bold, color = item.colorIcon)
                        Text(text = item.title, modifier = Modifier, fontSize = 16.sp, color = ColorCustom.secondText)
                    }
                    }

                }
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    navController.navigate(Routes.ManagerStudent)
                },
                    modifier = Modifier.weight(1f).shadow(
                        elevation =8.dp,
                        shape = RoundedCornerShape(12.dp),
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorCustom.primary
                    )){
                    Text("Quản lý sinh viên", fontSize = 16.sp)
                }
                Button(onClick = { navController.navigate(Routes.PostManagement)},
                    modifier = Modifier.weight(1f).shadow(
                        elevation =8.dp,
                        shape = RoundedCornerShape(12.dp),
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorCustom.primary
                    )){
                    Text("Quản lý bài viết", fontSize = 16.sp)
                }
            }
        }


    }

}
@Preview(showBackground = true)
@Composable
fun GreetingaPreview() {
    Uth_hubTheme {
        ManagerProfile(rememberNavController())
    }
}