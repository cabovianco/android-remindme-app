package com.cabovianco.remindme.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.cabovianco.remindme.R

val CherryRegular = FontFamily(
    Font(R.font.cherry_bomb_one_regular)
)

val Typography = Typography(
    displayMedium = TextStyle(
        fontFamily = CherryRegular,
        fontSize = 48.sp
    )
)