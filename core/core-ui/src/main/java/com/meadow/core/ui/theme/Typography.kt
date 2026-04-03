package com.meadow.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.meadow.core.ui.R

private val FontBubble = FontFamily(
    Font(R.font.bubble_round, FontWeight.Normal)
)

private val FontHandwritten = FontFamily(
    Font(R.font.cute_handwritten, FontWeight.Normal)
)

private val FontScript = FontFamily(
    Font(R.font.sparkly_script, FontWeight.Normal)
)

val MeadowTypography = Typography(

    displayLarge = TextStyle(
        fontFamily = FontScript,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        lineHeight = 40.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontScript,
        fontWeight = FontWeight.Normal
    ),
    displaySmall = TextStyle(
        fontFamily = FontScript,
        fontWeight = FontWeight.Normal
    ),

    headlineLarge = TextStyle(
        fontFamily = FontBubble,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 30.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontBubble,
        fontWeight = FontWeight.Medium
    ),
    headlineSmall = TextStyle(
        fontFamily = FontBubble,
        fontWeight = FontWeight.Medium
    ),

    titleLarge = TextStyle(
        fontFamily = FontBubble,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 26.sp
    ),

    titleMedium = TextStyle(
        fontFamily = FontBubble,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),

    titleSmall = TextStyle(
        fontFamily = FontBubble,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = FontHandwritten,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = FontHandwritten,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontHandwritten,
        fontWeight = FontWeight.Normal
    ),

    labelLarge = TextStyle(
        fontFamily = FontHandwritten,
        fontWeight = FontWeight.Medium
    ),
    labelMedium = TextStyle(
        fontFamily = FontHandwritten,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontHandwritten,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )

)

object TypeRoles {
    val ButtonText = MeadowTypography.labelMedium
    val ChipText = MeadowTypography.labelSmall
    val CardTitle = MeadowTypography.titleLarge
    val CardBody = MeadowTypography.bodyMedium
    val DialogTitle = MeadowTypography.titleLarge
    val DialogBody = MeadowTypography.bodyLarge
    val TooltipText = MeadowTypography.labelSmall
    val InputLabel = MeadowTypography.labelMedium
    val InputHint = MeadowTypography.labelSmall
}