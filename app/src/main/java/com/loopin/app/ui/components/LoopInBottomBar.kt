package com.loopin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.annotation.DrawableRes
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.BorderLight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite

enum class LoopInTab(
    val title: String,
    @DrawableRes val iconRes: Int,
    @DrawableRes val selectedIconRes: Int = iconRes
) {
    HOME("Home", FluentIcons.Home, FluentIcons.HomeFilled),
    DISCOVER("Discover", FluentIcons.Discover, FluentIcons.DiscoverFilled),
    NEW("New", FluentIcons.New, FluentIcons.NewFilled),
    MY_SUBS("My Subs", FluentIcons.MySubs, FluentIcons.MySubsFilled),
    PROFILE("Profile", FluentIcons.Profile, FluentIcons.ProfileFilled)
}

@Composable
fun LoopInBottomBar(
    selectedTab: LoopInTab = LoopInTab.HOME,
    onTabSelected: (LoopInTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PureWhite)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = BorderLight, thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoopInTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val itemColor = if (isSelected) ElectricBlue else NeutralGray

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = isSelected,
                            onClick = { onTabSelected(tab) },
                            role = Role.Tab
                        )
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = if (isSelected) tab.selectedIconRes else tab.iconRes),
                        contentDescription = tab.title,
                        tint = itemColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = tab.title,
                        color = itemColor,
                        fontFamily = AppFontFamily,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
