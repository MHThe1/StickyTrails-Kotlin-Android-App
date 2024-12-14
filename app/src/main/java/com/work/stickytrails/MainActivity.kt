package com.work.stickytrails

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.work.stickytrails.ui.screens.LoginScreen
import com.work.stickytrails.ui.screens.HomeScreen
import com.work.stickytrails.ui.theme.StickyTrailsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StickyTrailsTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    // Navigation controller to manage app navigation
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home") // Navigate to the home screen upon successful login
            })
        }

        // Home Screen
        composable("home") {
            HomeScreen(onLogout = {
                navController.navigate("login") // Navigate back to login screen on logout
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    StickyTrailsTheme {
        MainApp()
    }
}
