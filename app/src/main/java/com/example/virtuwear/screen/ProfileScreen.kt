package com.example.virtuwear.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.virtuwear.R
import com.example.virtuwear.components.AboutUsItem
import com.example.virtuwear.components.StatProfileItem
import com.example.virtuwear.viewmodel.ProfileViewModel


@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userResponse by profileViewModel.userResponse.observeAsState()
    LaunchedEffect (Unit) {
        profileViewModel.getDashboardById()
    }
    LaunchedEffect(userResponse) {
        userResponse?.let {
            if (it.isSuccessful) {
                Log.d("ProfileScreen", "User data from API: ${it.body()}")
            } else {
                Log.e("ProfileScreen", "API Error: ${it.code()} - ${it.message()}")
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        //Coins
        Row (
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface (
                modifier = Modifier.padding(end = 10.dp),
                color = Color(0xFFEAECF0),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = userResponse?.body()?.token?.toString() ?: "Loading Data",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier
                    .size(35.dp)
                    .background(Color(0xFFF5F5F5)),

                ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(24.dp)
                )
            }
        }

        // User name
        Text(
            text = userResponse?.body()?.name ?: "Loading Data",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // referral
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface (
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.background(Color(0xFFF5F5F5)),
            ) {
                Text(
                    text = userResponse?.body()?.referral?.referralCode?: "Loading Data",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(5.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.wrapContentSize()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.copy_outline),
                    contentDescription = "Copy",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

        }

        // Bookmark button
        Button (
            onClick = { navController.navigate("bookmark") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        ) {
            Surface (
                color = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .size(35.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    tint = Color.White,
                    contentDescription = "Bookmark",
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Bookmark",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                ),
            thickness = 1.dp,
            color = Color.Black
        )
        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatProfileItem(count = userResponse?.body()?.referral?.totalUsed?.toString() ?: "Loading Data", label = "Invitation", modifier = Modifier.weight(1f))
            StatProfileItem(count = userResponse?.body()?.totalTryon?.toString() ?: "Loading Data", label = "Total Try on", modifier = Modifier.weight(1f))
            StatProfileItem(count = userResponse?.body()?.totalGenerate?.toString() ?: "Loading Data", label = "Total Generate", modifier = Modifier.weight(1f))
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                ),
            thickness = 1.dp,
            color = Color.Black
        )

        // Menu items
        AboutUsItem(
            icon = Icons.Default.Create,
            title = "Reedem Code"
        )

        AboutUsItem(
            icon = Icons.Default.Star,
            title = "Rate Us"
        )

        AboutUsItem(
            icon = Icons.Outlined.Share,
            title = "Share with Friends"
        )

        AboutUsItem(
            iconContent = {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = Color.LightGray
                ) {
                    Text(
                        text = "i",
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            },
            title = "About Us"
        )

        AboutUsItem(
            iconContent = {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.LightGray
                ) {
                    Text(
                        text = "...",
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            },
            title = "Privacy & Policy"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Delete account button
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "Delete Account",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}




//@Preview(showBackground = true)
//@Composable
//fun PreviewProfileScreen() {
//    val navController = rememberNavController()
//    val userService = UserService
//    val profileViewModel = ProfileViewModel(userService)
//    ProfileScreen(navController = navController, profileViewModel = profileViewModel)
//}


