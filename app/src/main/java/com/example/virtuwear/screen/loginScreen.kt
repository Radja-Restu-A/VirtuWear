package com.example.virtuwear.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.virtuwear.R
import com.example.virtuwear.components.Header
import com.example.virtuwear.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider


@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), onLoginSuccess: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val googleSignInClient = remember { viewModel.getGoogleSignInClient() }

    val googleIcon = painterResource(id = R.drawable.google_logo)
    val loginImage = painterResource(id = R.drawable.login_image)
    val backgroundLogin = painterResource(id = R.drawable.background_login)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = viewModel.getCurrentUser()
                    if (user != null) {
                        user.email?.let { viewModel.insertProfile(it, 0) }
                    }
                    FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                        ?.addOnCompleteListener { task: Task<GetTokenResult> ->
                            if (task.isSuccessful) {
                                val idToken = task.result.token
                                Log.d("ID_TOKEN", idToken!!)
                                user?.uid?.let { uid ->
                                    user.email?.let { email ->
                                        user.displayName?.let { name ->
                                            viewModel.sendTokenToBackend(uid, email, name)
                                        }
                                    }
                                }
                            }
                        }
                    onLoginSuccess()
                } else {
                    Log.e("LoginScreen", "Google Sign-In failed", task.exception)
                }
            }
        } catch (e: Exception) {
            Log.e("LoginScreen", "Google Sign-In failed", e)
        }
    }

    Header("VirtuWear")

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(500.dp)
        ) {
            Image(
                painter = backgroundLogin,
                contentDescription = "Background Login",
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = loginImage,
                contentDescription = "Login Icon",
                modifier = Modifier.size(280.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Start Doing Experiments!",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.size(160.dp),
            fontSize = 20.sp,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(0.dp))

        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
            modifier = Modifier
                .width(340.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
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
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
    }
}