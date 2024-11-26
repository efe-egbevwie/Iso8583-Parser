package com.efe.iso8583tools.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import isotools.composeapp.generated.resources.*
import isotools.composeapp.generated.resources.Poppins_Light
import isotools.composeapp.generated.resources.Poppins_Medium
import isotools.composeapp.generated.resources.Poppins_Regular
import isotools.composeapp.generated.resources.Res

@Composable
fun bodyFontFamily() = FontFamily(
    Font(resource = Res.font.Poppins_Light, weight = FontWeight.Light),
    Font(resource = Res.font.Poppins_Regular, weight = FontWeight.Normal),
    Font(resource = Res.font.Poppins_Medium, weight = FontWeight.Medium),
    Font(resource = Res.font.Poppins_SemiBold, weight = FontWeight.SemiBold),
    Font(resource = Res.font.Poppins_Bold, weight = FontWeight.Bold)
)

@Composable
fun displayFontFamily() = FontFamily(
    Font(resource = Res.font.OpenSans_Light, weight = FontWeight.Light),
    Font(resource = Res.font.OpenSans_Regular, weight = FontWeight.Normal),
    Font(resource = Res.font.OpenSans_Medium, weight = FontWeight.Medium),
    Font(resource = Res.font.OpenSans_SemiBold, weight = FontWeight.SemiBold),
    Font(resource = Res.font.OpenSans_Bold, weight = FontWeight.Bold)
)

// Default Material 3 typography values
val baseline = Typography()

@Composable
fun AppTypography() = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily()),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily()),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily()),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily()),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily()),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily()),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily()),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily()),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily()),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily()),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily()),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily()),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily()),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily()),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily()),
)


