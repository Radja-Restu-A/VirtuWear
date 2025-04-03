package com.example.virtuwear.screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.virtuwear.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Berhasil")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("upload") }
                ) {
                    Text(text = "Upload")
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        loginViewModel.getGoogleSignInClient().signOut().addOnCompleteListener {
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Logout", color = Color(red = 255, green = 0, blue = 0))
                }
            }
        }
    }
}
