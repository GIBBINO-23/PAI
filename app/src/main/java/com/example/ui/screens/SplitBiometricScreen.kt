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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.SplitBill
import com.example.data.models.SplitMember
import com.example.ui.PayWiseViewModel
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.EmeraldSafeBg
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightElevated
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.PayPalBlue
import com.example.ui.theme.PayPalBlueBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SplitBiometricScreen(
    viewModel: PayWiseViewModel,
    modifier: Modifier = Modifier
) {
    val splitBills by viewModel.splitBills.collectAsState()
    var showCreateSplitDialog by remember { mutableStateOf(false) }

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PayPalBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallSplit,
                            contentDescription = "Split",
                            tint = ElectricCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Split & Biometric Pay",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Split group bills with 1-tap reminders & biometric auth",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = { showCreateSplitDialog = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PayPalBlueBright)
                        .testTag("create_split_bill_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Split", tint = Color.White)
                }
            }
        }

        item {
            // One-Tap Biometric Payment Demo Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("biometric_quick_demo_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PayPal Fast Biometric Auth",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Authorize payments securely with fingerprint or face scan without entering passwords.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            viewModel.openBiometricPayment(
                                title = "PayPal Biometric Demo Transaction",
                                amount = 35.00,
                                recipient = "Merchant Express POS"
                            ) {
                                viewModel.showToast("Biometric payment authorized successfully!")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color(0xFF003544)),
                        modifier = Modifier.testTag("test_biometric_demo_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Auth", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "Active Split Groups (${splitBills.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(splitBills) { bill ->
            SplitBillCard(
                bill = bill,
                onSendReminder = { memberName, amount ->
                    viewModel.sendReminderPing(memberName, amount)
                },
                onMarkPaid = { memberId, memberName ->
                    viewModel.markMemberPaid(bill.id, memberId, memberName)
                },
                onPayYourShare = {
                    val share = bill.totalAmount / (bill.members.size + 1)
                    viewModel.openBiometricPayment(
                        title = "Your Share for ${bill.title}",
                        amount = share,
                        recipient = "Group Split Pot"
                    ) {
                        viewModel.showToast("Your share of $${String.format("%.2f", share)} was authorized via biometrics!")
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCreateSplitDialog) {
        CreateSplitDialog(
            onDismiss = { showCreateSplitDialog = false },
            onConfirm = { title, amount, members ->
                viewModel.createSplitBill(title, amount, members)
                showCreateSplitDialog = false
            }
        )
    }
}

@Composable
fun SplitBillCard(
    bill: SplitBill,
    onSendReminder: (String, Double) -> Unit,
    onMarkPaid: (String, String) -> Unit,
    onPayYourShare: () -> Unit
) {
    val paidCount = bill.members.count { it.isPaid }
    val totalMembers = bill.members.size
    val progress = if (totalMembers > 0) paidCount.toFloat() / totalMembers else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("split_bill_${bill.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightElevated)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = bill.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Created ${bill.createdAt} • ${bill.members.size + 1} people",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Text(
                    text = "$${String.format("%.2f", bill.totalAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = ElectricCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Settlement Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Settlement Progress", fontSize = 11.sp, color = TextSecondary)
                Text(
                    text = "$paidCount of $totalMembers collected",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (progress == 1f) EmeraldSafe else TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress == 1f) EmeraldSafe else PayPalBlueBright,
                trackColor = MidnightSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Member list
            bill.members.forEach { member ->
                SplitMemberRow(
                    member = member,
                    onSendReminder = { onSendReminder(member.name, member.shareAmount) },
                    onMarkPaid = { onMarkPaid(member.id, member.name) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pay your share button
            Button(
                onClick = onPayYourShare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PayPalBlue.copy(alpha = 0.25f), contentColor = ElectricCyan)
            ) {
                Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                val myShare = bill.totalAmount / (bill.members.size + 1)
                Text(
                    text = "Pay Your Share ($${String.format("%.2f", myShare)}) with Biometrics",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SplitMemberRow(
    member: SplitMember,
    onSendReminder: () -> Unit,
    onMarkPaid: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MidnightSurface)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (member.isPaid) EmeraldSafeBg else MidnightElevated),
                contentAlignment = Alignment.Center
            ) {
                if (member.isPaid) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmeraldSafe, modifier = Modifier.size(16.dp))
                } else {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = member.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = "${member.paypalHandle} • $${String.format("%.2f", member.shareAmount)}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (member.isPaid) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldSafe.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "PAID", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldSafe)
                }
            } else {
                IconButton(onClick = onSendReminder, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Send Reminder",
                        tint = AmberWarning,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = onMarkPaid,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MidnightElevated, contentColor = TextPrimary),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(text = "Mark Paid", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun CreateSplitDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, amount: Double, members: List<Pair<String, String>>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var friendName by remember { mutableStateOf("") }
    var friendHandle by remember { mutableStateOf("") }

    val members = remember {
        mutableListOf(
            "Alex" to "@alex_pay",
            "Sam" to "@sam_wallet"
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MidnightSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Split Payment Calculator",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Calculate equal shares instantly across participants",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                // 1. Total Amount First
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("1. Enter Total Bill Amount ($)") },
                    placeholder = { Text("e.g. 150.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("split_total_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = Color(0xFF334155)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Number of Shares
                var sharesCount by remember { mutableStateOf(3) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MidnightElevated)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "2. Number of Shares:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "How many ways to split?", fontSize = 11.sp, color = TextMuted)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MidnightSurface)
                                .clickable { if (sharesCount > 2) sharesCount-- },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "-", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "$sharesCount",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MidnightSurface)
                                .clickable { if (sharesCount < 12) sharesCount++ },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Calculated Per Person Share Card
                val enteredAmount = amountStr.toDoubleOrNull() ?: 0.0
                val perPerson = if (sharesCount > 0 && enteredAmount > 0.0) enteredAmount / sharesCount else 0.0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldSafe.copy(alpha = 0.12f))
                        .border(1.dp, EmeraldSafe.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Calculated Share Per Person:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSafe)
                        Text(
                            text = "$${String.format("%.2f", perPerson)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = EmeraldSafe
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Description (e.g. Dinner, Uber, Groceries)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = Color(0xFF334155)
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextMuted)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            if (amt > 0.0) {
                                val splitMembers = (1 until sharesCount).map { i ->
                                    "Friend $i" to "@friend_$i"
                                }
                                onConfirm(title.ifBlank { "Group Expense" }, amt, splitMembers)
                            }
                        },
                        enabled = (amountStr.toDoubleOrNull() ?: 0.0) > 0.0,
                        colors = ButtonDefaults.buttonColors(containerColor = PayPalBlueBright, contentColor = Color.White)
                    ) {
                        Text("Create Split ($${String.format("%.2f", perPerson)}/ea)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
