package com.loopin.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

// ============================================================================
// Loop'in Brand Theme
// Electric Blue (#0070FF), Deep Midnight (#050D26), Inter Tight Typography
// ============================================================================

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = PureWhite,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = PureWhite,
    secondary = NeutralGray,
    onSecondary = PureWhite,
    background = PureWhite,
    onBackground = DeepMidnight,
    surface = PureWhite,
    onSurface = DeepMidnight,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = NeutralGray,
    outline = BorderLight,
    error = DangerRed,
    onError = PureWhite
)

@Composable
fun LoopInTheme(
    content: @Composable () -> Unit
) {
    val colors = LoopInColors()
    val shapes = LoopInShapes()
    val spacing = LoopInSpacing()

    CompositionLocalProvider(
        LocalLoopInColors provides colors,
        LocalLoopInShapes provides shapes,
        LocalLoopInSpacing provides spacing
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = LoopInTypography,
            content = content
        )
    }
}

// Convenient accessor object for semantic tokens
object LoopInTheme {
    val colors: LoopInColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLoopInColors.current

    val typography: androidx.compose.material3.Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes: LoopInShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalLoopInShapes.current

    val spacing: LoopInSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalLoopInSpacing.current
}
