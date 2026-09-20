package com.loopin.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// ============================================================================
// Loop'in Shape System
// ============================================================================

@Immutable
data class LoopInShapes(
    val card: Shape = RoundedCornerShape(24.dp),
    val tile: Shape = RoundedCornerShape(16.dp),
    val smallCard: Shape = RoundedCornerShape(14.dp),
    val pill: Shape = CircleShape,
    val bottomBar: Shape = RoundedCornerShape(32.dp)
)

val LocalLoopInShapes = staticCompositionLocalOf { LoopInShapes() }
