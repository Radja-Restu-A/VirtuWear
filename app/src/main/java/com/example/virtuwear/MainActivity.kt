package com.example.virtuwear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.virtuwear.ui.theme.VirtuWearTheme
import com.example.virtuwear.data.OnboardingManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()

        val onboardingManager = OnboardingManager(applicationContext)

        lifecycleScope.launch {
            val onboardingDone = onboardingManager.isOnboardingCompleted()
            val isUserLoggedIn = auth.currentUser != null
            val startDestination = when {
                !onboardingDone -> Screen.OnBoarding.route
                isUserLoggedIn -> Screen.Upload.route
                else -> Screen.Login.route
            }

            setContent {
                VirtuWearTheme {
                    AppNavHost(
                        isUserLoggedIn = isUserLoggedIn,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
