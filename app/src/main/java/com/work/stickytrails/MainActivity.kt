package com.work.stickytrails

import android.os.Bundle
import android.util.Log
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
import com.work.stickytrails.utils.TokenManager

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
    val tokenManager = TokenManager(navController.context)
    val savedToken = tokenManager.getToken() // Retrieve saved token

    // logging
    Log.d("TokenManager", "Saved Token: $savedToken")



    // Decide the start destination based on token presence
    val startDestination = if (savedToken.isNullOrEmpty()) "login" else "home"

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = { token ->
                    // Save token and navigate to HomeScreen
                    TokenManager(navController.context).saveToken(token)
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable("home") {
            HomeScreen(onLogout = {
                // Clear token on logout
                TokenManager(navController.context).clearToken()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
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
