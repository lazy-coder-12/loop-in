package com.loopin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.loopin.app.ui.theme.AmountLargeStyle
import com.loopin.app.ui.theme.AmountRowStyle
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.OverlineStyle

/**
 * Visual Showcase of the Loop'in Design System ("Old Money" Aesthetic).
 * Open this in Android Studio Split / Design View to inspect all semantic tokens,
 * typography scales, and reusable components in one place.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesignSystemShowcase(modifier: Modifier = Modifier) {
    val colors = LoopInTheme.colors
    val shapes = LoopInTheme.shapes
    val spacing = LoopInTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Title
        Text(
            text = "Loop'in Design System",
            style = LoopInTheme.typography.headlineLarge,
            color = colors.textPrimary
        )
        Text(
            text = "Bottle Green, Warm Ivory & Earthy Semantics · Light Mode",
            style = LoopInTheme.typography.bodyMedium,
            color = colors.textSecondary
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        // --------------------------------------------------------------------
        // 1. Color Palette Tokens
        // --------------------------------------------------------------------
        SectionHeader("1. Color Palette Tokens")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shapes.card)
                .background(colors.surface)
                .border(1.dp, colors.border, shapes.card)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ColorRow("Primary (Bottle Green)", colors.primary, colors.onPrimary, "#1C332B")
            ColorRow("Background (Warm Ivory)", colors.background, colors.textPrimary, "#F8F5EE")
            ColorRow("Surface (Pure Ivory)", colors.surface, colors.textPrimary, "#FFFDF8")
            ColorRow("Surface Variant", colors.surfaceVariant, colors.textSecondary, "#F0EBDF")
            ColorRow("Border Hairline", colors.border, colors.textSecondary, "#E4DDCE")
            ColorRow("Success (Sage)", colors.successContainer, colors.successContent, "#DDEBDD")
            ColorRow("Warning (Amber)", colors.warningContainer, colors.warningContent, "#F5E6C4")
            ColorRow("Information (Slate Blue)", colors.infoContainer, colors.infoContent, "#E1E9F0")
            ColorRow("Failed (Rust)", colors.failed, Color.White, "#A64B2A")
            ColorRow("Danger (Oxblood)", colors.danger, Color.White, "#7A1F2B")
        }

        Spacer(modifier = Modifier.height(spacing.xl))

        // --------------------------------------------------------------------
        // 2. Typography Scale
        // --------------------------------------------------------------------
        SectionHeader("2. Typography Scale")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shapes.card)
                .background(colors.surface)
                .border(1.dp, colors.border, shapes.card)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TypeSample("Display Large", "₹4,281", LoopInTheme.typography.displayLarge)
            TypeSample("Display Medium", "Monthly spend", LoopInTheme.typography.displayMedium)
            TypeSample("Headline Large", "Loop'in Wordmark", LoopInTheme.typography.headlineLarge)
            TypeSample("Headline Medium", "Recent Subscriptions", LoopInTheme.typography.headlineMedium)
            TypeSample("Title Large", "Netflix Premium", LoopInTheme.typography.titleLarge)
            TypeSample("Title Medium", "Monthly · Due 21 Sep", LoopInTheme.typography.titleMedium)
            TypeSample("Body Large", "Loop'in is watching for pre-debit notices.", LoopInTheme.typography.bodyLarge)
            TypeSample("Body Medium", "Supporting secondary description", LoopInTheme.typography.bodyMedium)
            TypeSample("Body Small", "Tertiary subtle disclaimer", LoopInTheme.typography.bodySmall)
            TypeSample("Label Large", "Add your first subscription", LoopInTheme.typography.labelLarge)
            TypeSample("Label Medium", "Auto · Review", LoopInTheme.typography.labelMedium)
            TypeSample("Overline", "COMING UP THIS WEEK", OverlineStyle)
        }

        Spacer(modifier = Modifier.height(spacing.xl))

        // --------------------------------------------------------------------
        // 3. Reusable Components
        // --------------------------------------------------------------------
        SectionHeader("3. Reusable Components")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shapes.card)
                .background(colors.surface)
                .border(1.dp, colors.border, shapes.card)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Monograms
            Text("Monogram Tiles (16dp Radius, Letter Monograms)", style = LoopInTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MonogramTile(name = "Netflix")
                MonogramTile(name = "Spotify")
                MonogramTile(name = "Amazon")
                MonogramTile(name = "Notion")
                MonogramTile(name = "Claude")
            }

            HorizontalDivider(color = colors.border)

            // Status Badges
            Text("Status Badges (Icon + Text, Never Color Alone)", style = LoopInTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(status = BadgeStatusType.REVIEW)
                StatusBadge(status = BadgeStatusType.ACTIVE)
                StatusBadge(status = BadgeStatusType.PAUSED)
                StatusBadge(status = BadgeStatusType.ENDED)
            }

            HorizontalDivider(color = colors.border)

            // Source Pills
            Text("Source Pills (Auto Detection vs Manual Entry)", style = LoopInTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SourcePill(source = SubscriptionSourceType.AUTO)
                SourcePill(source = SubscriptionSourceType.MANUAL)
            }

            HorizontalDivider(color = colors.border)

            // Currency Numerals
            Text("Currency Text (Tabular Figures, 60% Dimmed Symbol & Decimals)", style = LoopInTheme.typography.titleSmall)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Hero size: ", style = LoopInTheme.typography.bodyMedium, color = colors.textSecondary)
                    CurrencyText(
                        amountMinor = 428100L, // ₹4,281
                        style = AmountLargeStyle,
                        color = colors.primary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Row size (with paise): ", style = LoopInTheme.typography.bodyMedium, color = colors.textSecondary)
                    CurrencyText(
                        amountMinor = 12499950L, // ₹1,24,999.50
                        style = AmountRowStyle,
                        color = colors.textPrimary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lakh/Crore grouping: ", style = LoopInTheme.typography.bodyMedium, color = colors.textSecondary)
                    CurrencyText(
                        amountMinor = 1000000000L, // ₹1,00,00,000 (1 crore)
                        style = AmountRowStyle,
                        color = colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = LoopInTheme.typography.titleLarge,
        color = LoopInTheme.colors.textPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ColorRow(name: String, container: Color, content: Color, hex: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(LoopInTheme.shapes.smallCard)
            .background(container)
            .border(1.dp, LoopInTheme.colors.border, LoopInTheme.shapes.smallCard)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = LoopInTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = content
        )
        Text(
            text = hex,
            style = LoopInTheme.typography.bodySmall,
            color = content.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun TypeSample(label: String, sample: String, style: androidx.compose.ui.text.TextStyle) {
    Column {
        Text(text = label, style = OverlineStyle, color = LoopInTheme.colors.textTertiary)
        Text(text = sample, style = style, color = LoopInTheme.colors.textPrimary)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1200)
@Composable
fun DesignSystemShowcasePreview() {
    LoopInTheme {
        DesignSystemShowcase()
    }
}
