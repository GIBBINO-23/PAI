package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PayWiseViewModel
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
fun DemoGuideScreen(
    viewModel: PayWiseViewModel,
    modifier: Modifier = Modifier
) {
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
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Demo Hub",
                        tint = ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Hackathon Pitch & Demo Hub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Ready-to-present pitch deck, video script & judge presets",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            // Pitch Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hackathon_pitch_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PayPal Hackathon Project Pitch",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "PayWise AI solves the two largest pain points in modern digital transactions: digital payment scams and reckless spending creep. By placing Google Gemini 3.8 Flash inside the PayPal payment lifecycle, every scanned QR code is verified for fraud, every shopping cart item is audited against true user requirements, and every checkout is protected by biometric validation.",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            // 2-Minute Demo Video Recording Script
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightElevated)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VideoCameraFront, contentDescription = null, tint = EmeraldSafe, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2-Minute Video Demo Script",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSafe
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    DemoStepItem(
                        number = "0:00 - 0:25",
                        title = "Copilot Home & Expense Calculation",
                        desc = "Show real-time monthly burn ($386.63), categorized breakdown bars, and 3 AI money-saving recommendations formulating how to save $170/mo."
                    )

                    DemoStepItem(
                        number = "0:25 - 0:55",
                        title = "QR Scam Shield Live Detection",
                        desc = "Tap '🚨 Malicious Phishing QR' preset. Show Gemini deconstructing the reverse-refund trick, flagging 94% Risk Score, and blocking payment."
                    )

                    DemoStepItem(
                        number = "0:55 - 1:20",
                        title = "Smart Requirement Cart",
                        desc = "Show the $249 keyboard flagged as 'Overkill Spec' with a $45 alternative saving $204. Tap 'Checkout with Biometrics' to show the fingerprint radar."
                    )

                    DemoStepItem(
                        number = "1:20 - 1:45",
                        title = "Split Pay & Biometrics",
                        desc = "Show group dinner bill split among 3 friends. Send reminder notification and pay your share via the animated biometric sensor."
                    )

                    DemoStepItem(
                        number = "1:45 - 2:00",
                        title = "Debt Payoff & 24/7 Chatbot",
                        desc = "Show the upcoming loan payment alert (6 days) with Avalanche vs Snowball optimization, and ask the 24/7 AI chatbot a question."
                    )
                }
            }
        }

        item {
            // Interactive 1-Tap Trigger Buttons for Judges
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightElevated)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "1-Tap Live Demo Jumps",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.loadPresetPhishingScam() },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PayPalBlue.copy(alpha = 0.25f), contentColor = Color(0xFFEF4444))
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Jump to Scam Shield: Phishing QR Test", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.loadPresetCartAudit() },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PayPalBlue.copy(alpha = 0.25f), contentColor = ElectricCyan)
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Jump to Smart Cart: AI Requirement Audit", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.openBiometricPayment(
                                title = "PayPal Express Biometric Checkout",
                                amount = 49.99,
                                recipient = "Verified PayPal Merchant"
                            ) {
                                viewModel.showToast("Biometric payment authorized successfully!")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PayPalBlueBright, contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Launch Biometric Fingerprint Sensor", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Tech Stack & Hackathon Highlights
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MidnightSurface)
                    .padding(14.dp)
            ) {
                Text(text = "Technical Architecture Highlights:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "• Gemini 3.8 Flash (`Firebase.ai`) for semantic fraud & requirement auditing", fontSize = 11.sp, color = TextSecondary)
                Text(text = "• App Check cryptographic attestation for secure backend communications", fontSize = 11.sp, color = TextSecondary)
                Text(text = "• Jetpack Compose Material 3 with Obsidian Dark Fintech aesthetic", fontSize = 11.sp, color = TextSecondary)
                Text(text = "• High-precision deterministic heuristics + AI multi-vector risk engine", fontSize = 11.sp, color = TextSecondary)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DemoStepItem(number: String, title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PayPalBlue.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = number, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
    }
}
