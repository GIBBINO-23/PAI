package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthUiState
import com.example.ui.AuthViewModel
import com.example.ui.OnboardingStep
import com.example.ui.SecurityAuthMethod
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.MidnightElevated
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PayPalBlue
import com.example.ui.theme.PayPalBlueBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val step by authViewModel.onboardingStep.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Branding Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(PayPalBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PayWise AI",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "PayPal Intelligent Copilot & Fraud Guardian",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicatorDot(isActive = step == OnboardingStep.SIGN_IN, isDone = step > OnboardingStep.SIGN_IN)
                StepIndicatorDot(isActive = step == OnboardingStep.LINK_BANK, isDone = step > OnboardingStep.LINK_BANK)
                StepIndicatorDot(isActive = step == OnboardingStep.SECURITY_SETUP, isDone = step > OnboardingStep.SECURITY_SETUP)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Step Content
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_steps"
            ) { currentStep ->
                when (currentStep) {
                    OnboardingStep.SIGN_IN -> {
                        SignInStepCard(
                            authState = authState,
                            onGoogleSignIn = { authViewModel.signInWithGoogle(context) },
                            onDemoSignIn = { authViewModel.signInAsDemoUser() }
                        )
                    }
                    OnboardingStep.LINK_BANK -> {
                        LinkBankStepCard(
                            onConfirmBank = { bankName, last4 ->
                                authViewModel.completeBankLinking(bankName, last4)
                            }
                        )
                    }
                    OnboardingStep.SECURITY_SETUP -> {
                        SecuritySetupStepCard(
                            onConfirmSecurity = { method, pin ->
                                authViewModel.completeSecuritySetup(method, pin)
                            }
                        )
                    }
                    OnboardingStep.COMPLETED -> {
                        Box {}
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicatorDot(isActive: Boolean, isDone: Boolean) {
    Box(
        modifier = Modifier
            .size(if (isActive) 24.dp else 10.dp, 10.dp)
            .clip(CircleShape)
            .background(
                when {
                    isDone -> EmeraldSafe
                    isActive -> ElectricCyan
                    else -> MidnightElevated
                }
            )
    )
}

@Composable
fun SignInStepCard(
    authState: AuthUiState,
    onGoogleSignIn: () -> Unit,
    onDemoSignIn: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("onboarding_signin_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B374A))
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Step 1: Sign in with Google",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Connect your Google Mail account to authenticate and sync your PayPal AI agent.",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign-In Button
            Button(
                onClick = onGoogleSignIn,
                enabled = authState !is AuthUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("google_signin_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1F1F1F)
                )
            ) {
                if (authState is AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF1F1F1F), strokeWidth = 2.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "G",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF4285F4)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sign in with Google Mail",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Demo Sign-In
            Button(
                onClick = onDemoSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("demo_signin_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MidnightSurface,
                    contentColor = ElectricCyan
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Text(
                    text = "Quick Demo Sign-In (Judge Mode)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }

            if (authState is AuthUiState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = authState.message,
                    fontSize = 11.sp,
                    color = Color(0xFFEF4444),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun LinkBankStepCard(
    onConfirmBank: (bankName: String, last4: String) -> Unit
) {
    val banks = listOf(
        "Chase Premier Checking",
        "Bank of America Advantage",
        "Wells Fargo Everyday Checking",
        "PayPal Balance Direct",
        "Citibank Priority"
    )

    var selectedBank by remember { mutableStateOf(banks[0]) }
    var accountNumber by remember { mutableStateOf("8924") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("onboarding_bank_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B374A))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Step 2: Link Bank Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Link your preferred bank account to enable biometric checkouts & split settlements.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Select Financial Institution:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))

            banks.forEach { bank ->
                val isSelected = selectedBank == bank
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PayPalBlue.copy(alpha = 0.25f) else MidnightSurface)
                        .border(
                            1.dp,
                            if (isSelected) ElectricCyan else Color(0xFF2B374A),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedBank = bank }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bank,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = TextPrimary
                    )
                    if (isSelected) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = accountNumber,
                onValueChange = { if (it.length <= 4) accountNumber = it },
                label = { Text("Account Last 4 Digits") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = Color(0xFF334155)
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { onConfirmBank(selectedBank, accountNumber.ifBlank { "8924" }) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_bank_link_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PayPalBlueBright, contentColor = Color.White)
            ) {
                Text("Link Account & Continue", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun SecuritySetupStepCard(
    onConfirmSecurity: (method: SecurityAuthMethod, pin: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(SecurityAuthMethod.BIOMETRIC) }
    var pin by remember { mutableStateOf("1234") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("onboarding_security_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B374A))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Step 3: Security & Payment Auth",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose your preferred authentication method to authorize all future payments.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Method A: Biometrics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedMethod == SecurityAuthMethod.BIOMETRIC) PayPalBlue.copy(alpha = 0.25f) else MidnightSurface)
                    .border(1.dp, if (selectedMethod == SecurityAuthMethod.BIOMETRIC) ElectricCyan else Color(0xFF2B374A), RoundedCornerShape(12.dp))
                    .clickable { selectedMethod = SecurityAuthMethod.BIOMETRIC }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Biometric Authentication (Recommended)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text(text = "Authorize payments instantly with fingerprint or facial scan.", fontSize = 11.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Method B: Password / PIN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedMethod == SecurityAuthMethod.PASSWORD_PIN) PayPalBlue.copy(alpha = 0.25f) else MidnightSurface)
                    .border(1.dp, if (selectedMethod == SecurityAuthMethod.PASSWORD_PIN) ElectricCyan else Color(0xFF2B374A), RoundedCornerShape(12.dp))
                    .clickable { selectedMethod = SecurityAuthMethod.PASSWORD_PIN }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Security Password / PIN", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text(text = "Enter your secret 4-digit code for every transaction.", fontSize = 11.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 6) pin = it },
                label = { Text("Set 4-Digit Security PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = Color(0xFF334155)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onConfirmSecurity(selectedMethod, pin.ifBlank { "1234" }) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("complete_onboarding_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PayPalBlueBright, contentColor = Color.White)
            ) {
                Text("Complete Setup & Enter PayWise", fontWeight = FontWeight.Bold)
            }
        }
    }
}
