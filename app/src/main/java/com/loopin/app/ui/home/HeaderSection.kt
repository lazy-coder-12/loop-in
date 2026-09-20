package com.loopin.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.R
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.PureWhite

@Composable
fun HeaderSection(
    userName: String,
    greeting: String,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Avatar: Real photo from design with squircle white border
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(BorderStroke(2.dp, PureWhite), RoundedCornerShape(18.dp))
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar_user),
                contentDescription = "Profile Photo of $userName",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Greeting and Full Name ("Good Afternoon!" and "Anurag Verma")
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = greeting,
                color = PureWhite.copy(alpha = 0.85f),
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
            Text(
                text = userName,
                color = PureWhite,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 26.sp
            )
        }

        // Action Buttons: Search & Notification Bell
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderActionButton(
                icon = Icons.Rounded.Search,
                contentDescription = "Search",
                onClick = onSearchClick
            )
            Spacer(modifier = Modifier.width(10.dp))
            HeaderActionButton(
                icon = Icons.Rounded.Notifications,
                contentDescription = "Notifications",
                onClick = onNotificationsClick
            )
        }
    }
}

@Composable
private fun HeaderActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clickable(
                role = Role.Button,
                onClickLabel = contentDescription
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0x33050D26)), // Translucent dark midnight circle
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = PureWhite,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
