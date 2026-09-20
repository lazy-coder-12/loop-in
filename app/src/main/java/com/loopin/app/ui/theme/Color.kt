package com.loopin.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================================
// Loop'in Brand Palette
// Primary: #0070FF (Electric Blue)
// Midnight: #050D26 (Deep Midnight Black/Blue)
// Neutral Gray: #767676
// Border: #E6E6E6
// Pure White: #FFFFFF
// Surface Light: #F0F0F0
// ============================================================================

val ElectricBlue = Color(0xFF0070FF)
val DeepMidnight = Color(0xFF050D26)
val NeutralGray = Color(0xFF767676)
val BorderLight = Color(0xFFE6E6E6)
val PureWhite = Color(0xFFFFFFFF)
val SurfaceLight = Color(0xFFF0F0F0)

// Supporting accents
val AccentGreen = Color(0xFF00E676)
val WarningAmberContainer = Color(0xFFFFF3D6)
val WarningAmberContent = Color(0xFF8C5300)
val DangerRed = Color(0xFFFF4D4F)

// Pastel monogram color pairs (background, text)
val MonogramBlueBg = Color(0xFFD3E4FF)
val MonogramBlueText = Color(0xFF004CB4)

val MonogramCoralBg = Color(0xFFFFD5D5)
val MonogramCoralText = Color(0xFFB3261E)

val MonogramGreenBg = Color(0xFFD0F0C0)
val MonogramGreenText = Color(0xFF1E6023)

val MonogramPurpleBg = Color(0xFFEADDFF)
val MonogramPurpleText = Color(0xFF4F378B)

val MonogramAmberBg = Color(0xFFFFE0B2)
val MonogramAmberText = Color(0xFF795548)

@Immutable
data class LoopInColors(
    val primary: Color = ElectricBlue,
    val onPrimary: Color = PureWhite,
    val background: Color = PureWhite,
    val surface: Color = PureWhite,
    val surfaceVariant: Color = SurfaceLight,
    val border: Color = BorderLight,
    val textPrimary: Color = DeepMidnight,
    val textSecondary: Color = NeutralGray,
    val textTertiary: Color = NeutralGray.copy(alpha = 0.7f),
    val darkCard: Color = DeepMidnight,
    val onDarkCard: Color = PureWhite,
    val accentGreen: Color = AccentGreen,
    val successContainer: Color = MonogramGreenBg,
    val successContent: Color = MonogramGreenText,
    val warningContainer: Color = WarningAmberContainer,
    val warningContent: Color = WarningAmberContent,
    val failed: Color = DangerRed,
    val danger: Color = DangerRed,
    val infoContainer: Color = MonogramBlueBg,
    val infoContent: Color = MonogramBlueText,
    val isLight: Boolean = true
)

val LocalLoopInColors = staticCompositionLocalOf { LoopInColors() }
