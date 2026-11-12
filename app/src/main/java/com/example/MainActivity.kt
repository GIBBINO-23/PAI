package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AuthUiState
import com.example.ui.AuthViewModel
import com.example.ui.OnboardingStep
import com.example.ui.PayWiseViewModel
import com.example.ui.ScreenTab
import com.example.ui.components.BiometricModal
import com.example.ui.components.PayWiseBottomBar
import com.example.ui.components.PayWiseTopBar
import com.example.ui.screens.ChatbotScreen
import com.example.ui.screens.DebtTrackerScreen
import com.example.ui.screens.DemoGuideScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ScamShieldScreen
import com.example.ui.screens.SmartCartScreen
import com.example.ui.screens.SplitBiometricScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MidnightElevated
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PayWiseTheme
import com.example.ui.theme.TextPrimary
import com.example.util.configureAppCheck
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory Firebase App Check initialization before UI / Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            PayWiseTheme {
                PayWiseAppRoot()
            }
        }
    }
}

@Composable
fun PayWiseAppRoot(
    viewModel: PayWiseViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val onboardingStep by authViewModel.onboardingStep.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    val currentTab by viewModel.currentTab.collectAsState()
    val payPalBalance by viewModel.payPalBalance.collectAsState()
    val biometricState by viewModel.biometricState.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    // If onboarding is not completed, display the seamless Onboarding Wizard (Google Sign-In, Link Bank, Security Setup)
    if (onboardingStep != OnboardingStep.COMPLETED) {
        OnboardingScreen(authViewModel = authViewModel)
        return
    }

    // Handle back button to return to Copilot Home if on secondary screen
    BackHandler(enabled = currentTab != ScreenTab.HOME) {
        viewModel.selectTab(ScreenTab.HOME)
    }

    // Auto-dismiss in-app notification toasts
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3200)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg),
        containerColor = ObsidianBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            PayWiseTopBar(
                payPalBalance = payPalBalance,
                currentTab = currentTab,
                onOpenDemoHub = { viewModel.selectTab(ScreenTab.DEMO_GUIDE) }
            )
        },
        bottomBar = {
            PayWiseBottomBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.HOME -> HomeScreen(viewModel = viewModel)
                ScreenTab.SCAM_SHIELD -> ScamShieldScreen(viewModel = viewModel)
                ScreenTab.SMART_CART -> SmartCartScreen(viewModel = viewModel)
                ScreenTab.SPLIT_PAY -> SplitBiometricScreen(viewModel = viewModel)
                ScreenTab.DEBT_LOAN -> DebtTrackerScreen(viewModel = viewModel)
                ScreenTab.AI_CHAT -> ChatbotScreen(viewModel = viewModel)
                ScreenTab.DEMO_GUIDE -> DemoGuideScreen(viewModel = viewModel)
            }

            // In-app Notification Banner
            AnimatedVisibility(
                visible = toastMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                if (toastMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MidnightElevated)
                            .border(1.dp, ElectricCyan.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("notification_banner")
                    ) {
                        Text(
                            text = toastMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Global Biometric / PIN Payment Modal
            BiometricModal(
                state = biometricState,
                onStartScan = { viewModel.startBiometricScan() },
                onSubmitPin = { entered -> viewModel.submitPinPayment(entered) },
                onDismiss = { viewModel.closeBiometricModal() }
            )
        }
    }
}
