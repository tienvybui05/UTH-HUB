package com.example.uth_hub.core.design.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uth_hub.R
import com.example.uth_hub.core.design.theme.ColorCustom
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.Bookmark
import compose.icons.fontawesomeicons.regular.Comment
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Heart

public class PostOfStudent(
    var id: Int,
    var name: String,
    var styleName: String,
    var department: String,
    var email: String,
    var warning: Int,
    var avarta: Int,
    var date: String,
    var content: String,
    var images: List<Int> = emptyList() // 👈 thêm dòng này
)
@Composable
fun Post(){
    var isLiked by  remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var option by remember { mutableStateOf("") }
    var post by remember { mutableStateOf(
        PostOfStudent(
            1,
            "Đinh Quốc Đạt",
            "@dat123",
            "Công nghệ thông tin",
            "dat@gmail.com",0,
            R.drawable.avartardefault,
            "29/04/2007",
            "Xin chào mọi người" +
                    "Mình đang là sinh viên năm cuối và đang tìm cơ hội thực tập ở vị trí Intern để tiếp tục học hỏi thêm kỹ năng ." +
                    "Mình có đính kèm CV bên dưới, rất mong được anh/chị HR hoặc mọi người xem qua, góp ý giúp mình hoàn thiện hơn và hy vọng có cơ hội được phỏng vấn, học hỏi thêm ạ",
                    images = listOf(
                        R.drawable.avartardefault,
                        R.drawable.avartardefault,))) }
    Column(modifier = Modifier.fillMaxWidth().clip(shape = RoundedCornerShape(8.dp)).border(1.dp, ColorCustom.primary,RoundedCornerShape(8.dp)).background(color = ColorCustom.secondBackground).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(15.dp)){
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        ){
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Avartar(post.avarta)
            Column(){
                Text(text = post.styleName, fontSize = 18.sp , lineHeight = 16.sp,  color = ColorCustom.secondText)
                Text(text = post.department, fontSize = 14.sp, lineHeight = 13.sp, color = Color(0xFF595959), maxLines = Int.MAX_VALUE,
                    modifier = Modifier.width(250.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = post.date, fontSize = 13.sp, lineHeight = 14.sp, color = Color(0xFF595959))
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Clock,
                        contentDescription = "Ngày Đăng",
                        tint = Color(0xFF595959),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Box(){
            Icon(imageVector = Icons.Outlined.MoreVert,
                contentDescription = "Menu",
                tint = ColorCustom.secondText,
                modifier = Modifier.size(20.dp).clickable{
                    expanded = true
                })
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.clip(shape = RoundedCornerShape(8.dp)).background(color = ColorCustom.primary).padding(end = 10.dp, start = 10.dp)
            ) {
                DropdownMenuItem(
                    text = {
                        Row(modifier = Modifier.fillMaxWidth().background(color = Color.Transparent),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically){
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.ExclamationTriangle,
                                contentDescription = "Tố cáo bài viết vi phạm",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("Báo cáo bài viết vi phạm", fontSize = 14.sp, color = Color.White)
                        }

                           },
                        modifier = Modifier.background(color = ColorCustom.primary),
                    onClick = { option="Xin chào mọi người"
                        expanded = false}
                )
            }
        }

    }
    Column(modifier = Modifier.fillMaxWidth(),){
        Text(text = post.content, fontSize = 16.sp, lineHeight = 18.sp, color = ColorCustom.secondText, maxLines = Int.MAX_VALUE,
            modifier = Modifier.fillMaxWidth())
        if (post.images.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // hoặc 3 tùy bạn
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp).padding(top=10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(post.images) { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Ảnh bài đăng",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp)) // bo góc ảnh
//                            .border(
//                                1.dp,
//                                ColorCustom.primary,
//                                RoundedCornerShape(12.dp)
//                            )
                            .aspectRatio(1f) // giữ tỷ lệ vuông
                    )
                }
            }
        }

    }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row {
                IconButton(onClick = {
                    isLiked =!isLiked
                },
                    modifier = Modifier.width(80.dp))  {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)){
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Heart,
                            contentDescription = "Thích",
                            tint = if( isLiked) Color.Red else ColorCustom.secondText,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "500",fontSize = 16.sp, lineHeight = 18.sp, color = ColorCustom.secondText)
                    }


                }
                IconButton(onClick = {},
                    modifier = Modifier.width(80.dp))  {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(
                            imageVector = FontAwesomeIcons.Regular.Comment,
                            contentDescription = "Thích",
                            tint = ColorCustom.secondText,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "500",fontSize = 16.sp, lineHeight = 18.sp, color = ColorCustom.secondText)
                    }

                }
                IconButton(onClick = {},
                    modifier = Modifier.width(80.dp))  {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)){
                        Icon(
                            imageVector = FontAwesomeIcons.Regular.Bookmark,
                            contentDescription = "Thích",
                            tint = ColorCustom.secondText,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "500",fontSize = 16.sp, lineHeight = 18.sp, color = ColorCustom.secondText)
                    }

                }




            }
            Row() {  }
        }
    }

}
//@Preview(showBackground = true)
//@Composable
//fun PostManagementPreview() {
//    Uth_hubTheme {
//        PostManagement(rememberNavController())
//    }
//}