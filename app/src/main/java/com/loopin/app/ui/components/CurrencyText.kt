package com.loopin.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.loopin.app.domain.logic.IndianCurrencyFormatter
import com.loopin.app.ui.theme.AmountLargeStyle
import com.loopin.app.ui.theme.AmountRowStyle
import com.loopin.app.ui.theme.LoopInTheme

/**
 * CurrencyText renders amounts according to the Loop'in design specification:
 * - Medium weight typography with tabular figures (fontFeatureSettings "tnum").
 * - Rupee symbol and optional paise are rendered at 60% opacity.
 * - Whole rupee digits are rendered at full 100% opacity.
 * - Screen-reader accessible via explicit contentDescription.
 */
@Composable
fun CurrencyText(
    amountMinor: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = AmountRowStyle,
    color: Color = LoopInTheme.colors.textPrimary,
    symbolAlpha: Float = 0.6f,
    decimalAlpha: Float = 0.6f
) {
    val formatted = IndianCurrencyFormatter.split(amountMinor)
    val accessibleDescription = "${if (formatted.isNegative) "minus " else ""}Rupees ${formatted.wholePart}${
        if (formatted.decimalPart.isNotEmpty()) " and ${formatted.decimalPart.drop(1)} paise" else ""
    }"

    Row(
        modifier = modifier.semantics {
            contentDescription = accessibleDescription
        },
        verticalAlignment = Alignment.Bottom
    ) {
        // Negative sign if applicable
        if (formatted.isNegative) {
            Text(
                text = "-",
                style = style,
                color = color
            )
        }

        // Rupee Symbol (60% opacity) with subtle spacing
        Text(
            text = "${formatted.symbol} ",
            style = style.copy(
                fontSize = (style.fontSize.value * 0.85f).sp
            ),
            color = color.copy(alpha = symbolAlpha)
        )

        // Whole Rupee Digits (100% opacity)
        Text(
            text = formatted.wholePart,
            style = style,
            color = color
        )

        // Decimal Paise (60% opacity, only shown when non-zero)
        if (formatted.decimalPart.isNotEmpty()) {
            Text(
                text = formatted.decimalPart,
                style = style.copy(
                    fontSize = (style.fontSize.value * 0.78f).sp
                ),
                color = color.copy(alpha = decimalAlpha)
            )
        }
    }
}

@Preview
@Composable
fun CurrencyTextPreview() {
    LoopInTheme {
        Row {
            CurrencyText(
                amountMinor = 428100L, // ₹4,281
                style = AmountLargeStyle,
                color = LoopInTheme.colors.primary
            )
            CurrencyText(
                amountMinor = 12499950L, // ₹1,24,999.50
                style = AmountRowStyle,
                color = LoopInTheme.colors.textPrimary
            )
        }
    }
}
