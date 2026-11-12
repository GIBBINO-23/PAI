package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.RiskLevel
import com.example.data.models.ScamScanResult
import com.example.ui.PayWiseViewModel
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningBg
import com.example.ui.theme.CrimsonScam
import com.example.ui.theme.CrimsonScamBg
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
fun ScamShieldScreen(
    viewModel: PayWiseViewModel,
    modifier: Modifier = Modifier
) {
    val isAnalyzing by viewModel.isAnalyzingScam.collectAsState()
    val currentResult by viewModel.currentScamResult.collectAsState()
    val recentScans by viewModel.recentScans.collectAsState()

    var inputQrText by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 140f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserPosition"
    )

    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            viewModel.showToast("Camera access granted for QR scanning")
        }
    }

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
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = ElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI Scam & Fraud Shield",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Real-time inspection of UPI, QR codes & payment links",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            // Scanner Viewfinder Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qr_scanner_viewfinder"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B374A))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Viewfinder box
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MidnightSurface)
                            .border(2.dp, if (hasCameraPermission) ElectricCyan else Color(0xFF334155), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "QR Viewfinder",
                            tint = if (hasCameraPermission) ElectricCyan.copy(alpha = 0.5f) else TextMuted,
                            modifier = Modifier.size(80.dp)
                        )

                        if (hasCameraPermission) {
                            // Animated scanning laser
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .offset(y = (laserY - 70).dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color.Transparent, ElectricCyan, Color.Transparent)
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (hasCameraPermission) {
                        Text(
                            text = "Camera Active • Point at any merchant QR code or UPI link",
                            fontSize = 11.sp,
                            color = EmeraldSafe,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Button(
                            onClick = { cameraLauncher.launch(Manifest.permission.CAMERA) },
                            modifier = Modifier.height(38.dp).testTag("request_camera_permission_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PayPalBlueBright, contentColor = Color.White)
                        ) {
                            Text(text = "Grant Camera Access to Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Manual URL or Scanned Payload Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Paste or Enter Payment Link / UPI Intent",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inputQrText,
                        onValueChange = { inputQrText = it },
                        placeholder = { Text("upi://pay?pa=... or https://paypal.me/...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scan_input_field"),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = Color(0xFF334155)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.scanPaymentTarget(inputQrText) },
                        enabled = inputQrText.isNotBlank() && !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("run_scam_analysis_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayPalBlueBright,
                            contentColor = Color.White
                        )
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini AI Analyzing...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Inspect Link & QR Safety", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Quick Test Cases Pills
            Column {
                Text(
                    text = "Hackathon Test Scenarios (1-Tap Simulation):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        ScamPresetChip(
                            label = "⚠️ Fake Refund UPI Scam",
                            color = CrimsonScam
                        ) {
                            inputQrText = "upi://pay?pa=refund-desk@okhdfcbank&pn=Amazon%20Customer%20Refund&am=1.00&cu=INR"
                            viewModel.scanPaymentTarget(inputQrText, "Amazon Customer Refund Desk", "refund-desk@okhdfcbank", 1.00)
                        }
                    }
                    item {
                        ScamPresetChip(
                            label = "🎁 Lottery Phishing Link",
                            color = CrimsonScam
                        ) {
                            inputQrText = "upi://pay?pa=kbc-reward-claim99@paytm&pn=KBC%20Lottery%20Prize&am=2500"
                            viewModel.scanPaymentTarget(inputQrText, "KBC Lottery Prize", "kbc-reward-claim99@paytm", 2500.00)
                        }
                    }
                    item {
                        ScamPresetChip(
                            label = "✅ Authentic Starbucks QR",
                            color = EmeraldSafe
                        ) {
                            inputQrText = "upi://pay?pa=starbucks.bill@hdfcbank&pn=Starbucks%20Coffee%20Store&am=380.00&cu=INR"
                            viewModel.scanPaymentTarget(inputQrText, "Starbucks Coffee Store", "starbucks.bill@hdfcbank", 380.00)
                        }
                    }
                    item {
                        ScamPresetChip(
                            label = "✅ Verified PayPal.me",
                            color = EmeraldSafe
                        ) {
                            inputQrText = "https://paypal.me/CoffeeRoasters/8.50"
                            viewModel.scanPaymentTarget(inputQrText, "Artisan Coffee Roasters", "CoffeeRoasters", 8.50)
                        }
                    }
                }
            }
        }

        // Active Scan Result Card
        if (currentResult != null) {
            item {
                ScamAnalysisDetailCard(
                    result = currentResult!!,
                    onAuthorizeSafePayment = {
                        viewModel.openBiometricPayment(
                            title = "Authorize Payment: ${currentResult!!.merchantOrEntity}",
                            amount = 14.50,
                            recipient = currentResult!!.payAddress
                        ) {
                            viewModel.showToast("Payment authorized safely!")
                        }
                    }
                )
            }
        }

        // Recent Scans History
        if (recentScans.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Scanned Links History",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            items(recentScans) { scan ->
                RecentScanRow(scan = scan)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ScamPresetChip(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
fun ScamAnalysisDetailCard(
    result: ScamScanResult,
    onAuthorizeSafePayment: () -> Unit
) {
    val isDanger = result.riskLevel == RiskLevel.HIGH_RISK_SCAM
    val isCaution = result.riskLevel == RiskLevel.CAUTION
    val isSafe = result.riskLevel == RiskLevel.SAFE

    val badgeColor = when {
        isDanger -> CrimsonScam
        isCaution -> AmberWarning
        else -> EmeraldSafe
    }

    val badgeBg = when {
        isDanger -> CrimsonScamBg
        isCaution -> AmberWarningBg
        else -> EmeraldSafeBg
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scam_analysis_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightElevated),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, badgeColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Verdict Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            isDanger -> Icons.Default.Dangerous
                            isCaution -> Icons.Default.Warning
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            isDanger -> "HIGH RISK SCAM"
                            isCaution -> "CAUTION ADVISED"
                            else -> "VERIFIED SAFE"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = badgeColor
                    )
                }

                // Risk Score Ring / Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Risk: ${result.riskScore}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Entity details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightSurface)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Target Type:", fontSize = 11.sp, color = TextMuted)
                    Text(text = result.targetType, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Merchant / Sender:", fontSize = 11.sp, color = TextMuted)
                    Text(text = result.merchantOrEntity, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Address / VPA:", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = result.payAddress.take(28),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ElectricCyan
                    )
                }
            }

            // Threat flags
            if (result.threatFlags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Threat Flags Detected:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonScam)
                Spacer(modifier = Modifier.height(6.dp))
                result.threatFlags.forEach { flag ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "•", color = CrimsonScam, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = flag, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Detailed Reasoning
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Gemini AI Threat Reasoning",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = result.aiDetailedAnalysis,
                fontSize = 12.sp,
                color = TextPrimary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Advice Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeBg.copy(alpha = 0.5f))
                    .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Advice: ${result.safetyAction}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            // If Safe, allow 1-tap checkout
            if (isSafe) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onAuthorizeSafePayment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("safe_biometric_pay_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldSafe,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verified Safe — Authorize via Biometrics", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun RecentScanRow(scan: ScamScanResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.merchantOrEntity,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = scan.targetType,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (scan.riskLevel) {
                            RiskLevel.HIGH_RISK_SCAM -> CrimsonScamBg
                            RiskLevel.CAUTION -> AmberWarningBg
                            RiskLevel.SAFE -> EmeraldSafeBg
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (scan.riskLevel) {
                        RiskLevel.HIGH_RISK_SCAM -> "Scam (${scan.riskScore}%)"
                        RiskLevel.CAUTION -> "Caution (${scan.riskScore}%)"
                        RiskLevel.SAFE -> "Safe"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (scan.riskLevel) {
                        RiskLevel.HIGH_RISK_SCAM -> CrimsonScam
                        RiskLevel.CAUTION -> AmberWarning
                        RiskLevel.SAFE -> EmeraldSafe
                    }
                )
            }
        }
    }
}
