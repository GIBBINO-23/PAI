package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DebtItem
import com.example.ui.PayWiseViewModel
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningBg
import com.example.ui.theme.CrimsonScam
import com.example.ui.theme.CrimsonScamBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.EmeraldSafeBg
import com.example.ui.theme.MidnightElevated
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.PayPalBlue
import com.example.ui.theme.PayPalBlueBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DebtTrackerScreen(
    viewModel: PayWiseViewModel,
    modifier: Modifier = Modifier
) {
    val debts by viewModel.debts.collectAsState()
    var selectedStrategy by remember { mutableStateOf("Avalanche") } // "Avalanche" or "Snowball"

    val totalBalance = debts.sumOf { it.remainingBalance }
    val totalOriginal = debts.sumOf { it.totalAmount }
    val monthlyPaymentDue = debts.filter { !it.isPaidThisMonth }.sumOf { it.minMonthlyPayment }
    val nextDueDays = debts.filter { !it.isPaidThisMonth }.minOfOrNull { it.daysUntilDue } ?: 30

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PayPalBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Debt Guardian",
                        tint = ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Debt & Loan Guardian",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Payment reminders & AI payoff accelerator",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            // Main Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("debt_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B374A))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Total Remaining Debt", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$${String.format("%.2f", totalBalance)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                        }

                        // Due Alert Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (nextDueDays <= 7) CrimsonScamBg else AmberWarningBg)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = if (nextDueDays <= 7) CrimsonScam else AmberWarning,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Due in $nextDueDays Days!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (nextDueDays <= 7) CrimsonScam else AmberWarning
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Principal Repaid Progress", fontSize = 11.sp, color = TextMuted)
                        val paidPercent = ((1.0 - (totalBalance / totalOriginal.coerceAtLeast(1.0))) * 100).toInt()
                        Text(text = "$paidPercent% Cleared", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSafe)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (1f - (totalBalance.toFloat() / totalOriginal.toFloat())).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldSafe,
                        trackColor = MidnightSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Due This Month", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "$${String.format("%.2f", monthlyPaymentDue)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Active Balances", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "${debts.size} Accounts",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }
                }
            }
        }

        item {
            // Strategy Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightElevated)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI Payoff Strategy Optimization",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row {
                            StrategyChip(
                                label = "Avalanche (High APR)",
                                isSelected = selectedStrategy == "Avalanche",
                                onClick = { selectedStrategy = "Avalanche" }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            StrategyChip(
                                label = "Snowball (Low Bal)",
                                isSelected = selectedStrategy == "Snowball",
                                onClick = { selectedStrategy = "Snowball" }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedStrategy == "Avalanche") {
                            "⚡ Avalanche Strategy: Directing extra funds to your PayPal Credit Line (22.9% APR) saves ~$1,420 in total interest and clears your debt 7 months faster."
                        } else {
                            "❄️ Snowball Strategy: Paying off your lowest balance ($1,140 PayPal Credit) first gives you an immediate mental win and simplifies your monthly billing."
                        },
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "Loan & Debt Accounts (${debts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(debts) { debt ->
            DebtItemCard(
                debt = debt,
                onPayInstallment = { viewModel.payDebt(debt) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StrategyChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PayPalBlueBright else MidnightSurface)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

@Composable
fun DebtItemCard(
    debt: DebtItem,
    onPayInstallment: () -> Unit
) {
    val progress = (debt.remainingBalance / debt.totalAmount).toFloat()
    val isDueSoon = debt.daysUntilDue <= 7 && !debt.isPaidThisMonth

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("debt_card_${debt.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightElevated),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDueSoon) CrimsonScam.copy(alpha = 0.5f) else Color(0xFF2B374A)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title & APR row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = debt.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Next Due: ${debt.dueDate}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PayPalBlue.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${debt.interestRate}% APR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = ElectricCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Balances
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Remaining", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = "$${String.format("%.2f", debt.remainingBalance)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Min Installment", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = "$${String.format("%.2f", debt.minMonthlyPayment)}/mo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ElectricCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isDueSoon) CrimsonScam else PayPalBlueBright,
                trackColor = MidnightSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (debt.isPaidThisMonth) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSafe, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Settled for this billing cycle", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSafe)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = if (isDueSoon) CrimsonScam else AmberWarning,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${debt.daysUntilDue} days remaining",
                            fontSize = 11.sp,
                            color = if (isDueSoon) CrimsonScam else AmberWarning,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onPayInstallment,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PayPalBlueBright, contentColor = Color.White),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("pay_debt_btn_${debt.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Pay $${debt.minMonthlyPayment.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
