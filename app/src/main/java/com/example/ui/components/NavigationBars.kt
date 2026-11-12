package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CallSplit
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ScreenTab
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.MidnightElevated
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.PayPalBlue
import com.example.ui.theme.PayPalBlueBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PayWiseTopBar(
    payPalBalance: Double,
    currentTab: ScreenTab,
    onOpenDemoHub: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidnightSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PayPalBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PayWise",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AI AGENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan
                        )
                    }
                }
                Text(
                    text = "PayPal Hackathon Edition",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Balance Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MidnightElevated)
                    .border(1.dp, Color(0xFF2B374A), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldSafe)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$${String.format("%.2f", payPalBalance)}",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Demo Hub shortcut
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (currentTab == ScreenTab.DEMO_GUIDE) ElectricCyan.copy(alpha = 0.3f)
                        else MidnightElevated
                    )
                    .border(
                        1.dp,
                        if (currentTab == ScreenTab.DEMO_GUIDE) ElectricCyan else Color(0xFF2B374A),
                        CircleShape
                    )
                    .clickable { onOpenDemoHub() }
                    .padding(8.dp)
                    .testTag("topbar_demo_hub_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Demo Hub",
                    tint = if (currentTab == ScreenTab.DEMO_GUIDE) ElectricCyan else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PayWiseBottomBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit
) {
    val items = listOf(
        ScreenTab.HOME,
        ScreenTab.SCAM_SHIELD,
        ScreenTab.SMART_CART,
        ScreenTab.SPLIT_PAY,
        ScreenTab.DEBT_LOAN,
        ScreenTab.AI_CHAT
    )

    NavigationBar(
        containerColor = MidnightSurface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = Color(0xFF1E2838))
            .testTag("bottom_nav_bar")
    ) {
        items.forEach { tab ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    val iconVector = when (tab) {
                        ScreenTab.HOME -> if (isSelected) Icons.Default.Dashboard else Icons.Outlined.Dashboard
                        ScreenTab.SCAM_SHIELD -> if (isSelected) Icons.Default.Shield else Icons.Outlined.Shield
                        ScreenTab.SMART_CART -> if (isSelected) Icons.Default.ShoppingCart else Icons.Outlined.ShoppingCart
                        ScreenTab.SPLIT_PAY -> if (isSelected) Icons.Default.CallSplit else Icons.Outlined.CallSplit
                        ScreenTab.DEBT_LOAN -> if (isSelected) Icons.Default.AccountBalance else Icons.Outlined.AccountBalance
                        ScreenTab.AI_CHAT -> if (isSelected) Icons.Default.Chat else Icons.Outlined.Chat
                        ScreenTab.DEMO_GUIDE -> if (isSelected) Icons.Default.PlayCircle else Icons.Outlined.PlayCircle
                    }
                    Icon(
                        imageVector = iconVector,
                        contentDescription = tab.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ElectricCyan,
                    selectedTextColor = ElectricCyan,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = PayPalBlue.copy(alpha = 0.25f)
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
