package com.fitme.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Aqua-green palette for a fresh, coastal-fit wellness look.
private val LightColors = lightColorScheme(
    primary = Color(0xFF1FA7A0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F8F5),
    onPrimaryContainer = Color(0xFF0B564F),
    secondary = Color(0xFF7CCFC8),
    secondaryContainer = Color(0xFFDCF9F4),
    onSecondaryContainer = Color(0xFF173C39),
    background = Color(0xFFF4FBFA),
    onBackground = Color(0xFF183D39),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF183D39),
    surfaceVariant = Color(0xFFEEF9F7),
    onSurfaceVariant = Color(0xFF5F7F7B),
    outline = Color(0xFFB5DDD7),
    outlineVariant = Color(0xFFD9F3F0),
    error = Color(0xFFB95D56)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8DE0D7),
    onPrimary = Color(0xFF103C39),
    primaryContainer = Color(0xFF1C6B66),
    onPrimaryContainer = Color(0xFFE3F8F5),
    secondary = Color(0xFF9FDCD2),
    secondaryContainer = Color(0xFF1C423F),
    background = Color(0xFF091F1D),
    onBackground = Color(0xFFF2FBFA),
    surface = Color(0xFF0F2C2B),
    onSurface = Color(0xFFF2FBFA),
    surfaceVariant = Color(0xFF1A3D3B),
    onSurfaceVariant = Color(0xFFCFEDE9),
    outline = Color(0xFF5AA19B),
    outlineVariant = Color(0xFF2A4F4D)
)

private val FitTypography = Typography(
    headlineLarge = TextStyle(fontFamily=FontFamily.Serif,fontSize=34.sp,fontWeight=FontWeight.Normal),
    headlineMedium = TextStyle(fontFamily=FontFamily.Serif,fontSize=28.sp,fontWeight=FontWeight.Normal),
    titleLarge = TextStyle(fontFamily=FontFamily.Serif,fontSize=22.sp,fontWeight=FontWeight.Normal),
    titleMedium = TextStyle(fontSize=16.sp,fontWeight=FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize=16.sp),
    bodyMedium = TextStyle(fontSize=14.sp),
    labelLarge = TextStyle(fontSize=13.sp,fontWeight=FontWeight.SemiBold)
)

@Composable fun FitMeTheme(darkTheme:Boolean,content: @Composable () -> Unit) {
    MaterialTheme(colorScheme=if(darkTheme)DarkColors else LightColors,typography=FitTypography,content=content)
}
