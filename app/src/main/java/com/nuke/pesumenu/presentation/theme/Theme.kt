package com.nuke.pesumenu.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.MotionScheme
import androidx.wear.compose.material3.Typography
import com.nuke.pesumenu.R

@OptIn(ExperimentalTextApi::class)
private val RobotoFlex = FontFamily(
    Font(
        resId = R.font.roboto_flex_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(450),
            FontVariation.width(100f)
        )
    ),
    Font(
        resId = R.font.roboto_flex_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(550),
            FontVariation.width(100f)
        )
    ),
    Font(
        resId = R.font.roboto_flex_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(650),
            FontVariation.width(100f)
        )
    ),
    Font(
        resId = R.font.roboto_flex_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(750),
            FontVariation.width(100f)
        )
    ),
    Font(
        resId = R.font.roboto_flex_variable,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(850),
            FontVariation.width(100f)
        )
    )
)

private val PESUTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 34.sp
    ),
    displayMedium = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 30.sp
    ),
    displaySmall = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Bold,
        fontSize = 23.sp,
        lineHeight = 27.sp
    ),
    titleLarge = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 15.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoFlex,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)

@Composable
fun PESUMenuTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = PESUTypography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
