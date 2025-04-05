package com.example.virtuwear.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.virtuwear.R
import com.example.virtuwear.components.header
import com.example.virtuwear.data.OnboardingManager
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen(navController: NavController) {
    var currentPage by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val backgroundImage1 = painterResource(id = R.drawable.background1)
    val backgroundImage2 = painterResource(id = R.drawable.background2)
    val onboardingImage1 = painterResource(id = R.drawable.onboarding1)
    val onboardingImage2 = painterResource(id = R.drawable.onboarding2)
    val onboardingManager = remember { OnboardingManager(context) }
    val scope = rememberCoroutineScope()

    header()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(525.dp)
        ) {
            Image(
                painter = if (currentPage == 1) backgroundImage1 else backgroundImage2,
                contentDescription = "Background Onboarding",
                modifier = Modifier.fillMaxSize()
            )

            Crossfade(
                targetState = currentPage,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                ),
                modifier = Modifier.size(280.dp)
            ) { page ->
                Image(
                    painter = if (page == 1) onboardingImage1 else onboardingImage2,
                    contentDescription = "Onboarding Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Crossfade(
            targetState = currentPage,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        ) { page ->
            Text(
                text = if (page == 1) "VirtuWear is here to assist you!" else "Wear clothes as you desire!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(300.dp),
                fontSize = 20.sp,
                lineHeight = 28.sp
            )
        }

        Crossfade(
            targetState = currentPage,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        ) { page ->
            Text(
                text = if (page == 1) "To help you try on clothes you want to buy online." else "You can also try on multiple outfits simultaneously on e-commerce.",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 8.dp),
                lineHeight = 20.sp
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(200.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    scope.launch {
                        onboardingManager.setOnboardingCompleted()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                }
            ) {
                Text(
                    text = "Skip",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = {
                    if (currentPage == 1) {
                        currentPage = 2
                    } else {
                        scope.launch {
                            onboardingManager.setOnboardingCompleted()
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = "Next",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}