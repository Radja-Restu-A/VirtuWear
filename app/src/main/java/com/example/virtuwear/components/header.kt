package com.example.virtuwear.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virtuwear.R

val font = FontFamily(Font(R.font.airbnb_cereal_w_xbd))

@Composable
fun Header(konten:String){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Spacer(modifier = Modifier.height(40.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.
            fillMaxWidth().
            height(50.dp).
            padding(top = 20.dp)
        ){
            Text(
                text = konten,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
                fontSize = 12.sp
            )
        }
    }
}