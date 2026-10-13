package com.lowlauncher.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    surface = PureBlack,
    onSurface = PureWhite,
    background = PureBlack,
    onBackground = PureWhite,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = SubtleGray,
    outline = BorderGray
)

@Composable
fun LowLauncherTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
