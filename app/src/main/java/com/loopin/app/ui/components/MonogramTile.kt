package com.loopin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.MonogramAmberBg
import com.loopin.app.ui.theme.MonogramAmberText
import com.loopin.app.ui.theme.MonogramBlueBg
import com.loopin.app.ui.theme.MonogramBlueText
import com.loopin.app.ui.theme.MonogramCoralBg
import com.loopin.app.ui.theme.MonogramCoralText
import com.loopin.app.ui.theme.MonogramGreenBg
import com.loopin.app.ui.theme.MonogramGreenText
import com.loopin.app.ui.theme.MonogramPurpleBg
import com.loopin.app.ui.theme.MonogramPurpleText
import kotlin.math.abs

/**
 * Letter monogram tile for subscriptions with soft pastel backgrounds (matching reference design).
 * Generates an intentional pastel tone based on the subscription name.
 */
@Composable
fun MonogramTile(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    cornerRadius: Dp? = null,
    customBg: Color? = null,
    customText: Color? = null
) {
    val initial = when {
        name.contains("Hotstar", ignoreCase = true) -> "H"
        else -> name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    }
    val shapes = LoopInTheme.shapes

    // Curated pastel palettes echoing the exact reference design (Hotstar Blue, Netflix Salmon, Spotify Mint)
    val colorPairs = listOf(
        Color(0xFFA5C9FF) to Color(0xFF050D26), // Hotstar blue
        Color(0xFFFFA6A6) to Color(0xFF050D26), // Netflix coral
        Color(0xFFBEE8B7) to Color(0xFF050D26), // Spotify mint
        Color(0xFFE5D4FF) to Color(0xFF050D26), // Purple
        Color(0xFFFFDFB0) to Color(0xFF050D26)  // Amber
    )

    val selectedPair = when {
        name.contains("Hotstar", ignoreCase = true) || name.contains("Disney", ignoreCase = true) -> colorPairs[0]
        name.contains("Netflix", ignoreCase = true) -> colorPairs[1]
        name.contains("Spotify", ignoreCase = true) -> colorPairs[2]
        else -> {
            val index = abs(name.hashCode()) % colorPairs.size
            colorPairs[index]
        }
    }

    val backgroundColor = customBg ?: selectedPair.first
    val textColor = customText ?: selectedPair.second

    val tileShape = if (cornerRadius != null) androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius) else shapes.tile

    Box(
        modifier = modifier
            .size(size)
            .clip(tileShape)
            .background(backgroundColor)
            .semantics {
                contentDescription = "$name monogram"
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = LoopInTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.40f).sp
            ),
            color = textColor
        )
    }
}

@Preview
@Composable
fun MonogramTilePreview() {
    LoopInTheme {
        Box(modifier = Modifier.background(Color.White)) {
            MonogramTile(name = "Disney + Hotstar")
        }
    }
}
