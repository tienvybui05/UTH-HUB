package com.example.uth_hub.Screens.Manager

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.R
import com.example.uth_hub.Screens.Shared.Avartar
import com.example.uth_hub.ui.theme.ColorCustom
import com.example.uth_hub.ui.theme.Uth_hubTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ChevronLeft
import compose.icons.fontawesomeicons.solid.Fingerprint
import compose.icons.fontawesomeicons.solid.Trash
import compose.icons.fontawesomeicons.solid.TrashAlt

public  class student(
    var id:Int,
    var name:String,
    var styleName:String,
    var department:String,
    var email:String,
    var warning:Int,
    var avarta:Int
){}

@Composable
fun ManagerStudent(navController: NavController){
  var textStyeName by remember { mutableStateOf("") }
    var listStudent = listOf(student(1,"Đinh Quốc Đạt","@dat123","Công nghệ hông tin","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tin","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tin","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tin","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tin","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tiniii iiiii iiiiiiii iiiiiiiii iiiii","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tin","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tin","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tin","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tin","dat@gmail.com",5,R.drawable.avartardefault),
        student(1,"Đinh Quốc Đạt","@dat123","Công nghệ thông tin","dat@gmail.com",5,R.drawable.avartardefault),
        )

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
                Text("Sinh viên", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ColorCustom.primary)
            }

        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = ColorCustom.primary)){}
        Row {
            TextField(
                value = textStyeName,
                onValueChange = {textStyeName =it},
                placeholder = {Text("Tìm kiếm")},
                modifier = Modifier.clip(shape = RoundedCornerShape(30.dp)).background(Color.White).padding(top=3.dp, bottom = 3.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFFFFFF),
                    unfocusedContainerColor = Color(0xFFF8F8F8),
                ),

                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Fingerprint,
                            contentDescription = "Tìm kiếm",
                            modifier = Modifier.size(20.dp),
                            tint = ColorCustom.primary
                        )
                    }
                }


            )
        }
        LazyColumn {
          items(listStudent){
              item -> Column {
                  Row(
                      modifier = Modifier.fillMaxWidth().padding(5.dp),
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                      Column() {
                          Avartar(item.avarta)
                      }

                      Column {
                                Row {
                                    Text(text = "Họ và tên: ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ColorCustom.secondText)
                                    Text(text = item.name, fontSize = 16.sp, maxLines = Int.MAX_VALUE,
                                        modifier = Modifier.width(170.dp) )
                                }
                              Row {
                                  Text(text = "Email: ", fontSize = 16.sp, fontWeight = FontWeight.Bold,color = ColorCustom.secondText)
                                  Text(text = item.email, fontSize = 16.sp, maxLines = Int.MAX_VALUE,
                                      modifier = Modifier.width(200.dp))
                              }
                              Row {
                                  Text(text = "Khoa: ", fontSize = 16.sp, fontWeight = FontWeight.Bold,color = ColorCustom.secondText)
                                  Text(text = item.department, fontSize = 16.sp, maxLines = Int.MAX_VALUE,
                                      modifier = Modifier.width(200.dp) )
                              }
                              Row {
                                  Text(text = "Số lần cảnh báo: ", fontSize = 16.sp, fontWeight = FontWeight.Bold,color = ColorCustom.primaryText)
                                  Text(text = "${item.warning}", fontSize = 16.sp )
                              }


                      }
                      Column {
                          IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = FontAwesomeIcons.Solid.Trash,
                                        contentDescription = "Xoá sinh viên",
                                        tint = Color.Red,
                                        modifier = Modifier.size(24.dp)
                                    )

                          }
                      }
                  }
              Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = ColorCustom.primary)){}
          }
          }

        }

    }

}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Uth_hubTheme {
        ManagerStudent(rememberNavController())
    }
}