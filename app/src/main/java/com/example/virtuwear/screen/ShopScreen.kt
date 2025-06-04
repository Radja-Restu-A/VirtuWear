package com.example.virtuwear.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.virtuwear.components.CoinCard
import com.example.virtuwear.viewmodel.ProfileViewModel
import com.example.virtuwear.viewmodel.PurchaseState
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Observe StateFlow dari ViewModel
    val purchaseState by viewModel.uiState.collectAsState()

    // Daftar paket koin
    val coinPacks = listOf(
        Triple("coin_pack_10", "10 Coin", "Rp 18.000"),
        Triple("coin_pack_20", "20 Coin", "Rp 35.000"),
        Triple("coin_pack_30", "30 Coin", "Rp 50.000"),
        Triple("coin_pack_40", "40 Coin", "Rp 65.000"),
        Triple("coin_pack_50", "50 Coin", "Rp 80.000")
    )


    // Ketika state berubah ke Success atau Error, munculkan snackbar
    LaunchedEffect(purchaseState) {
        when (purchaseState) {
            is PurchaseState.Success -> {
                // Ganti memanggil .message dengan teks tetap:
                snackbarHostState.showSnackbar("Purchase berhasil!")
                viewModel.resetState()
            }
            is PurchaseState.Error -> {
                val errMsg = (purchaseState as PurchaseState.Error).message
                snackbarHostState.showSnackbar("Error: $errMsg")
                viewModel.resetState()
            }
            else -> { /* Idle/Loading: no-op */ }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Shop",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Limited Offer (ambil coin_pack_20)
            Text(
                text = "Limited Offer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val limitedPack = coinPacks.first { it.first == "coin_pack_20" }
            CoinCard(
                coinAmount = limitedPack.second,
                price = limitedPack.third,
                isLimitedOffer = true,
                productId = limitedPack.first,
                onBuyClick = { productId ->
                    viewModel.purchaseCoin(
                        productId = productId,
                        purchaseToken = "", // ganti bila punya token IAP asli
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Best Deals",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(coinPacks.filter { it.first != "coin_pack_20" }) { (productId, coinAmount, price) ->
                    CoinCard(
                        coinAmount = coinAmount,
                        price = price,
                        isLimitedOffer = false,
                        productId = productId,
                        onBuyClick = { id ->
                            viewModel.purchaseCoin(
                                productId = id,
                                purchaseToken = "",
                            )
                        }
                    )
                }
            }
        }

        if (purchaseState is PurchaseState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}
