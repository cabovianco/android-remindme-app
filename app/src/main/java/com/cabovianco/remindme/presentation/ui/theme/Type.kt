package com.cabovianco.remindme.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cabovianco.remindme.R

val cherryRegular = FontFamily(
    Font(R.font.cherry_bomb_one_regular)
)

private val defaultFont = FontFamily(
    Font(R.font.fredoka_regular, FontWeight.Normal)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Normal,
        fontSize = 60.sp
    ),
    displayMedium = TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp
    ),
    displaySmall = TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = defaultFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = defaultFont,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = defaultFont,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = defaultFont,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = defaultFont,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = defaultFont,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = defaultFont,
        fontSize = 10.sp
    )
)
