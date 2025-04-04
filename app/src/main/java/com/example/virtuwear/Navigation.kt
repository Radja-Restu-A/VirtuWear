package com.example.virtuwear

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.virtuwear.screen.HomeScreen
import com.example.virtuwear.screen.LoginScreen
import com.example.virtuwear.screen.OnBoardingScreen
import com.example.virtuwear.screen.UploadPhotoScreen
import com.example.virtuwear.screen.DownloadScreen
sealed class Screen(val route: String) {
    object OnBoarding : Screen("onboarding")
    object Login : Screen("login")
    object Home : Screen("home")
    object Upload : Screen("upload")
}

@Composable
fun AppNavHost(isUserLoggedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (isUserLoggedIn) Screen.Home.route else Screen.OnBoarding.route

    NavHost(navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.OnBoarding.route) { OnBoardingScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Upload.route) { UploadPhotoScreen(navController) }
        // Bikin route buat passing garmentType
        composable(
            route = "download?garmentType={garmentType}",
            arguments = listOf(
                navArgument("garmentType") {
                    type = NavType.StringType
                    defaultValue = "Single Garment"
                }
            )
        ) { backStackEntry ->
            val garmentType = backStackEntry.arguments?.getString("garmentType") ?: "Single Garment"
            DownloadScreen(navController = navController, garmentType = garmentType)
        }
    }
}