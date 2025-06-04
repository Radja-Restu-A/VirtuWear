package com.example.virtuwear.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CoinCard(
    coinAmount: String,
    price: String,
    isLimitedOffer: Boolean,
    productId: String,
    onBuyClick: (String) -> Unit,
    textSpacing: Dp = 8.dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(2.dp, Color.Black),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(textSpacing)
        ) {
            Text(
                text = coinAmount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = price,
                fontSize = 16.sp,
                color = Color.Black
            )
            if (isLimitedOffer) {
                Text(
                    text = "Limited-time offer!",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .width(100.dp)
                    .border(2.dp, Color.Green)
                    .align(Alignment.CenterHorizontally)
                    .clickable { onBuyClick(productId) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BUY NOW",
                    color = Color.Green,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
