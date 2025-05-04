package com.example.virtuwear

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.virtuwear.screen.LoginScreen
import com.example.virtuwear.screen.OnBoardingScreen
import com.example.virtuwear.screen.ProfileScreen
import com.example.virtuwear.screen.UploadPhotoScreen
import com.example.virtuwear.screen.DownloadScreen
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Refresh
import com.example.virtuwear.screen.BookmarkScreen
import com.example.virtuwear.screen.HistoryScreen
import com.example.virtuwear.screen.GarmentDetailScreen


sealed class Screen(val route: String) {
    object OnBoarding : Screen("onboarding")
    object Login : Screen("login")
    object Upload : Screen("upload")
    object History : Screen("history")
    object Profile : Screen("profile")
    object GarmentDetail : Screen("garmentDetail/{idSingle}")
    object Download : Screen("download")
    object Bookmark : Screen("bookmark")
}

// Define bottom navigation items
sealed class BottomNavItem(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object History : BottomNavItem(
        route = Screen.History.route,
        title = "History",
        icon = { Icon(Icons.Filled.Refresh, contentDescription = "History") }
    )

    object Upload : BottomNavItem(
        route = Screen.Upload.route,
        title = "Home",
        icon = { Icon(Icons.Filled.Home, contentDescription = "Upload") }
    )

    object Profile : BottomNavItem(
        route = Screen.Profile.route,
        title = "Profile",
        icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") }
    )
}

@Composable
fun AppNavHost(isUserLoggedIn: Boolean, startDestination: String) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(
                bottom = paddingValues.calculateBottomPadding(),
            )
        ) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(Screen.Upload.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.OnBoarding.route) {
                OnBoardingScreen(navController = navController)
            }
            composable(Screen.Upload.route) {
                UploadPhotoScreen(navController = navController)
            }
            composable(Screen.History.route) {
                HistoryScreen(navController = navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(Screen.Download.route) {
                DownloadScreen(
                    navController = navController,
                    garmentType = "Single",
                    id = 19
                )
            }

            composable(Screen.Bookmark.route) {
                BookmarkScreen(navController = navController)
            }
            composable(
                route = Screen.GarmentDetail.route,
                arguments = listOf(navArgument("idSingle") { type = NavType.LongType })
            ) { backStackEntry ->
                val idSingle = backStackEntry.arguments?.getLong("idSingle") ?: 0L
                GarmentDetailScreen(
                    idSingle = idSingle,
                    onDismiss = { navController.popBackStack() }
                )
            }
            composable(
                route = "download?garmentType={garmentType}&id={id}",
                arguments = listOf(
                    navArgument("garmentType") { type = NavType.StringType },
                    navArgument("id") { type = NavType.LongType },
                )
            ) { backStackEntry ->
                val garmentType = backStackEntry.arguments?.getString("garmentType") ?: ""
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                DownloadScreen(
                    navController = navController,
                    garmentType = garmentType,
                    id = id,
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // state
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.History,
        BottomNavItem.Upload,
        BottomNavItem.Profile
    )

    val showBottomNavIn = listOf(
        Screen.History.route,
        Screen.Upload.route,
        Screen.Profile.route
    )

    // tampilkan atau jangan
    if (currentRoute in showBottomNavIn) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(36.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // History
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Upload",
                            tint = if (currentRoute == Screen.History.route) Color.Black else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Add button
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { navController.navigate(Screen.Upload.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            } },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddCircle,
                                contentDescription = "Add",
                                tint = Color.Black,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }

                    // Profile icon
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = if (currentRoute == Screen.Profile.route) Color.Black else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
