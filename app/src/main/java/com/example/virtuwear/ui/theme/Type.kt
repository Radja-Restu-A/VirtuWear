package com.example.virtuwear.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.virtuwear.R

val airbnbCerealFontFamily = FontFamily(
    Font(R.font.airbnb_cereal_w_bk, FontWeight.Normal),
    Font(R.font.airbnb_cereal_w_lt, FontWeight.Light),
    Font(R.font.airbnb_cereal_w_blk, FontWeight.Black),
    Font(R.font.airbnb_cereal_w_md,FontWeight.Medium),
    Font(R.font.airbnb_cereal_w_bd, FontWeight.Bold),
    Font(R.font.airbnb_cereal_w_xbd, FontWeight.ExtraBold)
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = airbnbCerealFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = airbnbCerealFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = airbnbCerealFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)