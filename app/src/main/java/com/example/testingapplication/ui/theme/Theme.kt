package com.example.testingapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color  // ✅ use compose Color, not android.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary          = Color(0xFF3D7BF5),
    background       = Color(0xFF1C2333),
    surface          = Color(0xFF2A3240),
    onPrimary        = Color.White,
    onBackground     = Color.White,
    onSurface        = Color.White,
    onSurfaceVariant = Color(0xFF8A94A6),
    outline          = Color(0xFF3D4E63),

)

private val LightColorScheme = lightColorScheme(
    primary          = Color(0xFF3D7BF5),
    background       = Color(0xFFF0F2F5),
    surface          = Color(0xFFFFFFFF),
    onPrimary        = Color.White,
    onBackground     = Color(0xFF1C2333),
    onSurface        = Color(0xFF1C2333),
    onSurfaceVariant = Color(0xFF5A6478),
    outline          = Color(0xFFDDE1E7)
)

@Composable
fun TestingApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,   // ✅ false — keeps your custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}