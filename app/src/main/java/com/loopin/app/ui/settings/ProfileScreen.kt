package com.loopin.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.annotation.DrawableRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.R
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.ui.components.LoopInBottomBar
import com.loopin.app.ui.components.LoopInPrimaryButton
import com.loopin.app.ui.components.LoopInTab
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DangerRed
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    repository: SubscriptionRepository,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ProfileContent(
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onSeedSampleData = {
            coroutineScope.launch {
                repository.seedSampleData()
                snackbarHostState.showSnackbar("Sample subscriptions loaded successfully!")
            }
        },
        onClearAllData = {
            coroutineScope.launch {
                repository.clearAllData()
                snackbarHostState.showSnackbar("All subscription data cleared.")
            }
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onTabSelected = onTabSelected
    )
}

@Composable
fun ProfileContent(
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSeedSampleData: () -> Unit = {},
    onClearAllData: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var showDataDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            LoopInBottomBar(
                selectedTab = LoopInTab.PROFILE,
                onTabSelected = onTabSelected
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .background(PureWhite)
        ) {
            // Header
            ProfileHeader(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // User Profile Row: Avatar photo + Name + Handle & Subs count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_user),
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Anurag Verma",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DeepMidnight
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "@anurag_verma • 12 subscriptions",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ElectricBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // "Edit Profile" Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F4F7))
                        .clickable { showDataDialog = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Edit),
                        contentDescription = "Edit Profile",
                        tint = DeepMidnight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Edit Profile",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = DeepMidnight
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Primary Menu Items with Leading Icons:
                // 1. Settings
                ProfileMenuItemWithIcon(
                    iconRes = FluentIcons.Settings,
                    title = "Settings",
                    subtitle = "Account Settings",
                    onClick = { showDataDialog = true }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 2. Analytics
                ProfileMenuItemWithIcon(
                    iconRes = FluentIcons.Trending,
                    title = "Analytics",
                    subtitle = "View expenses and breakdown",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Analytics breakdown coming soon!")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 3. Invite Friends
                ProfileMenuItemWithIcon(
                    iconRes = FluentIcons.Gift,
                    title = "Invite Friends",
                    subtitle = "Share your invite link with friends",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Invite link copied to clipboard!")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Secondary Menu Options (Text Rows with Horizontal Dividers)
                ProfileTextMenuItem(
                    title = "Help Center",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Loop'in 100% Offline Guide")
                        }
                    }
                )
                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                ProfileTextMenuItem(
                    title = "Share Feedback",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Thank you for your feedback!")
                        }
                    }
                )
                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                ProfileTextMenuItem(
                    title = "Legal",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("100% Private, Local-First Architecture")
                        }
                    }
                )
                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                ProfileTextMenuItem(
                    title = "Logout",
                    onClick = {
                        showDataDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // App Version Footer
                Text(
                    text = "App version 1.0",
                    fontFamily = AppFontFamily,
                    fontSize = 13.sp,
                    color = NeutralGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Data Management & Settings Dialog
    if (showDataDialog) {
        AlertDialog(
            onDismissRequest = { showDataDialog = false },
            title = {
                Text(
                    text = "Profile & Data Settings",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DeepMidnight
                )
            },
            text = {
                Column {
                    Text(
                        text = "Manage your local Room database or reset sample subscriptions.",
                        fontFamily = AppFontFamily,
                        fontSize = 14.sp,
                        color = NeutralGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LoopInPrimaryButton(
                        onClick = {
                            onSeedSampleData()
                            showDataDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Seed Sample Subscriptions",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            onClearAllData()
                            showDataDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
                    ) {
                        Text("Clear All Local Data", fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, color = DangerRed)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDataDialog = false }) {
                    Text("Done", fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, color = ElectricBlue)
                }
            },
            containerColor = PureWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ProfileMenuItemWithIcon(
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = title,
            tint = DeepMidnight,
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = AppFontFamily,
                fontSize = 13.sp,
                color = NeutralGray
            )
        }

        Icon(
            painter = painterResource(FluentIcons.ChevronRight),
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ProfileTextMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = DeepMidnight
        )
        Icon(
            painter = painterResource(FluentIcons.ChevronRight),
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ProfileHeader(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button: 48dp accessible touch target
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Navigate back"
                ) { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF2F4F7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(FluentIcons.Back),
                    contentDescription = "Back",
                    tint = DeepMidnight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Center Title & Subtitle
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Your Profile and settings",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = NeutralGray
            )
        }

        // Notification Bell Button: 48dp accessible touch target
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Open notifications"
                ) { onNotificationsClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PureWhite)
                    .border(
                        BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(FluentIcons.Alert),
                    contentDescription = "Notifications",
                    tint = DeepMidnight,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    LoopInTheme {
        ProfileContent()
    }
}
