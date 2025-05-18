package com.example.virtuwear.screen

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.virtuwear.components.PrivacyPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.virtuwear.R
import com.example.virtuwear.components.AboutUsItem
import com.example.virtuwear.components.Alert
import com.example.virtuwear.components.AlertType
import com.example.virtuwear.components.ReferralCodeDialog
import com.example.virtuwear.components.StatProfileItem
import com.example.virtuwear.viewmodel.LoginViewModel
import com.example.virtuwear.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userResponse by profileViewModel.userResponse.collectAsState()
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showReferralDialog by remember { mutableStateOf(false) }
    val redeemStatus by profileViewModel.redeemCodeStatus.collectAsState()
    var showAlreadyRedeemed by remember { mutableStateOf(false) }



    LaunchedEffect (Unit) {
        profileViewModel.getDashboardById()
    }


    userResponse?.let { user ->
        Log.d("ProfileScreen", "User data updated: $user")
    } ?: run {
        Log.e("ProfileScreen", "Failed to fetch user data")
    }



    LaunchedEffect(Unit) {
        profileViewModel.fetchUser()
    }


    PrivacyPolicy(
        showPrivacy = showPrivacyPolicy,
        onDismiss = { showPrivacyPolicy = false },
        onAccept = { },
        showCheckboxAndButton = false
    )

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
                    text = userResponse?.token?.toString() ?: "Loading Data",
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
            text = userResponse?.name ?: "Loading Data",
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
                    text = userResponse?.referral?.referralCode?: "Loading Data",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Button(
                onClick = {

                },
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
            StatProfileItem(count = userResponse?.referral?.totalUsed?.toString() ?: "Loading Data", label = "Invitation", modifier = Modifier.weight(1f))
            StatProfileItem(count = userResponse?.totalTryon?.toString() ?: "Loading Data", label = "Total Try on", modifier = Modifier.weight(1f))
            StatProfileItem(count = userResponse?.totalGenerate?.toString() ?: "Loading Data", label = "Total Generate", modifier = Modifier.weight(1f))
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
            title = "Reedem Code",
            onClick = {
                if (userResponse?.redeemedReferral != null) {
                    showAlreadyRedeemed = true
                } else {
                    showReferralDialog = true
                }
            }
        )

        if (showAlreadyRedeemed) {
            Alert(
                showDialog = true,
                onDismiss = { showAlreadyRedeemed = false },
                title = userResponse?.redeemedReferral.toString(),
                message = "You Already Redeemed A Referral Code",
                confirmButtonText = "Confirm",
                onConfirmClick = { showAlreadyRedeemed = false },
                type = AlertType.SUCCESS
            )
        }

        if (showReferralDialog) {
            println("Attempting to show dialog")

            ReferralCodeDialog(
                onDismiss = { showReferralDialog = false },
                onConfirm = { code ->
                    profileViewModel.redeemReferralCode(code)
                    println("Referral code submitted: $code")
                    showReferralDialog = false
                }
            )
        }

        redeemStatus?.let { result ->
            if (result.isSuccess) {
                Alert(
                    showDialog = true,
                    onDismiss = { profileViewModel.clearRedeemStatus() },
                    title = "Referral Code Redeemed",
                    message = "The code was valid. Your reward has been added.",
                    confirmButtonText = "Confirm",
                    onConfirmClick = { profileViewModel.clearRedeemStatus() },
                    type = AlertType.SUCCESS
                )
            } else if (result.isFailure) {
                Alert(
                    showDialog = true,
                    onDismiss = { profileViewModel.clearRedeemStatus() },
                    title = "Invalid Code",
                    message = "The referral code you entered is not valid.",
                    confirmButtonText = "Confirm",
                    onConfirmClick = { profileViewModel.clearRedeemStatus() },
                    type = AlertType.ERROR
                )
            }
        }


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
            title = "Privacy & Policy",
            onClick = { showPrivacyPolicy = true }
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
            title = "Delete Account",
            onClick = {  }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Log out account button
        Button(
            onClick = { showError = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp)
        ) {
            Text(
                text = "Log Out",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        if (showError) {
            Alert(
                showDialog = showError,
                onDismiss = { showError = false },
                title = "Log Out?",
                message = "Are you sure you want to log out from your account?",
                confirmButtonText = "Confirm",
                cancelButtonText = "Cancel",
                onConfirmClick = { FirebaseAuth.getInstance().signOut()
                    loginViewModel.getGoogleSignInClient().signOut().addOnCompleteListener {
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    } },
                onCancelClick = { showError = false },
                type = AlertType.CONFIRMATION
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


