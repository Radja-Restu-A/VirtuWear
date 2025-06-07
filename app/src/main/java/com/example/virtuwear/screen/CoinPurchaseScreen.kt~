package com.example.virtuwear.screen

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.billingclient.api.Purchase
import com.example.virtuwear.di.BillingClientWrapper
import com.example.virtuwear.viewmodel.LoginViewModel
import com.example.virtuwear.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun CoinPurchaseScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity
    val coroutineScope = rememberCoroutineScope()

    val firebase = loginViewModel.getCurrentUser()
    val userUid = firebase?.uid

    val onPurchaseSuccess = rememberUpdatedState<(Purchase, String) -> Unit> { purchase, productId ->
        coroutineScope.launch {
            profileViewModel.handleCoinPurchase(productId, purchase.purchaseToken,
                userUid.toString()
            )
        }
    }

    val billingClient = remember {
        BillingClientWrapper(context) { purchase, productId ->
            onPurchaseSuccess.value(purchase, productId)
        }
    }


    LaunchedEffect(Unit) {
        billingClient.startConnection {}
    }
    val coinProducts = listOf(
        "coin_pack_10",
        "coin_pack_20",
        "coin_pack_30",
        "coin_pack_40",
        "coin_pack_50"
    )


    Column {
        coinProducts.forEach { id ->
            Button(onClick = { billingClient.launchPurchase(activity, id) }) {
                Text("Buy $id")
            }
        }
    }
}

