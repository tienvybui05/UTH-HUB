package com.example.uth_hub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.uth_hub.app.navigation.BottomNavigationBar
import com.example.uth_hub.app.navigation.NavGraph
import com.example.uth_hub.core.design.theme.Uth_hubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Uth_hubTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Uth_hubTheme {
        MainApp()
    }
}