package com.example.virtuwear.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.virtuwear.R
import com.example.virtuwear.viewmodel.LoginViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.foundation.Image
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.virtuwear.ui.theme.black


@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), onLoginSuccess: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val googleSignInClient = remember { viewModel.getGoogleSignInClient() }
    val googleIcon = painterResource(id = R.drawable.google_logo)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onLoginSuccess()
                } else {
                    Log.e("LoginScreen", "Google Sign-In failed", task.exception)
                }
            }
        } catch (e: Exception) {
            Log.e("LoginScreen", "Google Sign-In failed", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize() // Mengisi seluruh layar
            .padding(bottom = 16.dp), // Jarak dari bawah
        verticalArrangement = Arrangement.Bottom, // Tempatkan button di bawah
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontal
    ) {
        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
            modifier = Modifier
                .width(340.dp) // Perkecil lebar button
                .height(50.dp) // Atur tinggi agar proporsional
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = black
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = googleIcon,
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Sign in with Google",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}