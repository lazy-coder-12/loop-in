package com.loopin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.PrimaryButtonGradient
import com.loopin.app.ui.theme.PureWhite

/**
 * Loop'in standard primary action button with linear gradient background (#3D3C3B to #16110F)
 * and pure white (#FFFFFF) typography.
 */
@Composable
fun LoopInPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    elevation: Dp = 0.dp,
    content: @Composable RowScope.() -> Unit
) {
    val bgModifier = if (enabled) {
        Modifier
            .clip(shape)
            .background(PrimaryButtonGradient)
    } else {
        Modifier
            .clip(shape)
            .background(Color(0xFFE5E7EB))
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.then(bgModifier),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = PureWhite,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color(0xFF9CA3AF)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            pressedElevation = if (elevation > 0.dp) elevation + 2.dp else 2.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = contentPadding
    ) {
        ProvideTextStyle(
            value = TextStyle(
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = if (enabled) PureWhite else Color(0xFF9CA3AF)
            )
        ) {
            content()
        }
    }
}
