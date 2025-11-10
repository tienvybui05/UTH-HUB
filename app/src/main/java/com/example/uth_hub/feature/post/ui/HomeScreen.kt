package com.example.uth_hub.feature.post.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.R
import com.example.uth_hub.core.design.components.Avartar
import com.example.uth_hub.core.design.components.DrawerMenu
import com.example.uth_hub.core.design.components.Post
import com.example.uth_hub.core.design.theme.ColorCustom
import com.example.uth_hub.core.design.theme.Uth_hubTheme
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(
                onSettingsClick = { /* TODO: Navigate to settings */ },
                onHelpClick = { /* TODO: Navigate to help */ }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().background(color = Color.White)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = ColorCustom.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(color = Color.White)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { scope.launch { drawerState.open() } }, modifier = Modifier.weight(1f)
                    ) {
                        Text("☰", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorCustom.primary)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.logouth),
                        contentDescription = "Logo Uth",
                        modifier = Modifier.weight(2f).height(40.dp)
                    )
                    Row (modifier = Modifier.weight(1f)){}
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Avartar(R.drawable.avartardefault)
                    Column {
                        Text(text = "@Buitienvy", fontSize = 16.sp, color = ColorCustom.secondText)
                        Text(
                            text = "Hôm nay có g hót ?",
                            fontSize = 13.sp,
                            color = Color(0xFF595959),
                            modifier = Modifier.clickable { /* onClick */ }
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(5) {
                        Post()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Uth_hubTheme {
        HomeScreen(rememberNavController())
    }
}