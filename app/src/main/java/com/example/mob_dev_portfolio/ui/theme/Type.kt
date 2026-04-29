package com.example.mob_dev_portfolio.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mob_dev_portfolio.R

val JetbrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold)
)

val Typography = Typography(

//    HEADLINE

    headlineLarge = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),

//    TITLE

    titleSmall = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),

    titleMedium = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),

    titleLarge = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),

//    BODY

    bodySmall = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),

//    LABEL

    labelSmall = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    ),

    labelMedium = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),

    labelLarge = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
)