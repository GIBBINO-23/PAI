package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.CartProduct
import com.example.data.models.ChatMessage
import com.example.data.models.DebtItem
import com.example.data.models.ExpenseCategory
import com.example.data.models.ExpenseItem
import com.example.data.models.SavingTip
import com.example.data.models.ScamScanResult
import com.example.data.models.SplitBill
import com.example.data.repository.PayWiseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScreenTab(val title: String, val iconName: String) {
    HOME("Copilot", "Dashboard"),
    SCAM_SHIELD("Scam Shield", "Shield"),
    SMART_CART("Smart Cart", "ShoppingCart"),
    SPLIT_PAY("Split & Bio", "CallSplit"),
    DEBT_LOAN("Debt Alert", "AccountBalance"),
    AI_CHAT("Chatbot", "Chat"),
    DEMO_GUIDE("Demo Hub", "PlayCircle")
}

data class BiometricPaymentState(
    val isOpen: Boolean = false,
    val title: String = "",
    val amount: Double = 0.0,
    val recipient: String = "",
    val isScanning: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val securityMethod: SecurityAuthMethod = SecurityAuthMethod.BIOMETRIC,
    val expectedPin: String = "1234"
)

class PayWiseViewModel(
    private val repository: PayWiseRepository = PayWiseRepository()
) : ViewModel() {

    // Tab Navigation
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // Data from repo
    val payPalBalance = repository.payPalBalance
    val expenses = repository.expenses
    val cartItems = repository.cartItems
    val savingTips = repository.savingTips
    val debts = repository.debts
    val splitBills = repository.splitBills
    val chatMessages = repository.chatMessages
    val recentScans = repository.recentScans

    // UI Loading & Scam Analysis States
    private val _isAnalyzingScam = MutableStateFlow(false)
    val isAnalyzingScam: StateFlow<Boolean> = _isAnalyzingScam.asStateFlow()

    private val _currentScamResult = MutableStateFlow<ScamScanResult?>(null)
    val currentScamResult: StateFlow<ScamScanResult?> = _currentScamResult.asStateFlow()

    private val _isGeneratingTips = MutableStateFlow(false)
    val isGeneratingTips: StateFlow<Boolean> = _isGeneratingTips.asStateFlow()

    private val _isChatbotThinking = MutableStateFlow(false)
    val isChatbotThinking: StateFlow<Boolean> = _isChatbotThinking.asStateFlow()

    private val _isAddingCartItem = MutableStateFlow(false)
    val isAddingCartItem: StateFlow<Boolean> = _isAddingCartItem.asStateFlow()

    // Biometric Modal State
    private val _biometricState = MutableStateFlow(BiometricPaymentState())
    val biometricState: StateFlow<BiometricPaymentState> = _biometricState.asStateFlow()

    // Notification toast banner
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        // Pre-generate initial saving tips on launch
        generateSavingTips()
    }

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    // Expense Calculation
    val totalMonthlyExpenses: Double
        get() = expenses.value.sumOf { it.amount }

    val categoryBreakdown: Map<ExpenseCategory, Double>
        get() = expenses.value.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

    fun addExpense(title: String, amount: Double, category: ExpenseCategory, merchant: String, recurring: Boolean) {
        repository.addExpense(title, amount, category, merchant, recurring)
        showToast("Logged $$amount expense for $title")
    }

    fun generateSavingTips() {
        viewModelScope.launch {
            _isGeneratingTips.value = true
            try {
                repository.refreshSavingTips()
            } catch (e: Exception) {
                // Ignore
            } finally {
                _isGeneratingTips.value = false
            }
        }
    }

    // Scam Shield
    fun scanPaymentTarget(input: String, merchant: String = "", vpa: String = "", amount: Double? = null) {
        if (input.isBlank()) return
        viewModelScope.launch {
            _isAnalyzingScam.value = true
            try {
                val result = repository.analyzePaymentTarget(input, merchant, vpa, amount)
                _currentScamResult.value = result
            } catch (e: Exception) {
                showToast("Failed to analyze: ${e.message}")
            } finally {
                _isAnalyzingScam.value = false
            }
        }
    }

    fun clearScamResult() {
        _currentScamResult.value = null
    }

    // Smart Cart
    fun addCartItem(name: String, price: Double, requirement: String, category: String) {
        viewModelScope.launch {
            _isAddingCartItem.value = true
            try {
                repository.addProductToCart(name, price, requirement, category)
                showToast("AI evaluated '$name' against your requirements!")
            } catch (e: Exception) {
                showToast("Error evaluating: ${e.message}")
            } finally {
                _isAddingCartItem.value = false
            }
        }
    }

    fun removeCartItem(id: String) {
        repository.removeCartItem(id)
        showToast("Item removed from cart")
    }

    fun checkoutCartWithPayPal(): Double {
        return repository.checkoutCartWithPayPal()
    }

    // Split Bills
    fun createSplitBill(title: String, amount: Double, members: List<Pair<String, String>>) {
        repository.createSplitBill(title, amount, members)
        showToast("Created split bill for '$title' ($$amount)")
    }

    fun markMemberPaid(billId: String, memberId: String, memberName: String) {
        repository.markSplitMemberPaid(billId, memberId)
        showToast("Recorded payment from $memberName")
    }

    fun sendReminderPing(memberName: String, amount: Double) {
        showToast("Sent PayPal reminder notification to $memberName ($${String.format("%.2f", amount)})")
    }

    // Debt & Loan Reminders
    fun payDebt(debt: DebtItem) {
        openBiometricPayment(
            title = "Pay Installment: ${debt.name}",
            amount = debt.minMonthlyPayment,
            recipient = "PayPal Loan Clearinghouse"
        ) {
            repository.payDebtInstallment(debt.id, debt.minMonthlyPayment)
            showToast("Successfully paid $$${debt.minMonthlyPayment} towards ${debt.name}")
        }
    }

    // Biometric Modal Flow
    private var onBiometricSuccessAction: (() -> Unit)? = null

    fun openBiometricPayment(
        title: String,
        amount: Double,
        recipient: String,
        method: SecurityAuthMethod = SecurityAuthMethod.BIOMETRIC,
        pin: String = "1234",
        onSuccess: () -> Unit
    ) {
        onBiometricSuccessAction = onSuccess
        _biometricState.value = BiometricPaymentState(
            isOpen = true,
            title = title,
            amount = amount,
            recipient = recipient,
            isScanning = false,
            isSuccess = false,
            errorMessage = null,
            securityMethod = method,
            expectedPin = pin
        )
    }

    fun submitPinPayment(entered: String) {
        if (entered == _biometricState.value.expectedPin) {
            viewModelScope.launch {
                _biometricState.value = _biometricState.value.copy(isSuccess = true, errorMessage = null)
                delay(700)
                onBiometricSuccessAction?.invoke()
                onBiometricSuccessAction = null
                delay(400)
                _biometricState.value = BiometricPaymentState(isOpen = false)
            }
        } else {
            _biometricState.value = _biometricState.value.copy(errorMessage = "Incorrect PIN. Try default '1234'.")
        }
    }

    fun startBiometricScan() {
        viewModelScope.launch {
            _biometricState.value = _biometricState.value.copy(isScanning = true, errorMessage = null)
            delay(1200) // Realistic biometric verification animation window
            _biometricState.value = _biometricState.value.copy(isScanning = false, isSuccess = true)
            delay(800)
            onBiometricSuccessAction?.invoke()
            onBiometricSuccessAction = null
            delay(400)
            _biometricState.value = BiometricPaymentState(isOpen = false)
        }
    }

    fun closeBiometricModal() {
        _biometricState.value = BiometricPaymentState(isOpen = false)
        onBiometricSuccessAction = null
    }

    // Chatbot
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isChatbotThinking.value = true
            try {
                repository.sendChatMessage(text)
            } catch (e: Exception) {
                showToast("Chat error: ${e.message}")
            } finally {
                _isChatbotThinking.value = false
            }
        }
    }

    // Hackathon Presets for Instant Demos
    fun loadPresetPhishingScam() {
        _currentTab.value = ScreenTab.SCAM_SHIELD
        val maliciousQr = "upi://pay?pa=refund-desk-urgent@okhdfcbank&pn=Amazon%20Customer%20Refund&am=1.00&cu=INR"
        scanPaymentTarget(maliciousQr, "Amazon Customer Refund Desk", "refund-desk-urgent@okhdfcbank", 1.00)
    }

    fun loadPresetLegitMerchant() {
        _currentTab.value = ScreenTab.SCAM_SHIELD
        val safeQr = "upi://pay?pa=starbucks.bill@hdfcbank&pn=Starbucks%20Coffee%20Store&am=14.50&cu=INR"
        scanPaymentTarget(safeQr, "Starbucks Coffee Store", "starbucks.bill@hdfcbank", 14.50)
    }

    fun loadPresetSplitDinner() {
        _currentTab.value = ScreenTab.SPLIT_PAY
        createSplitBill(
            title = "Hackathon Team Dinner & Coffee",
            amount = 168.00,
            members = listOf(
                "Dave (Backend)" to "@dave_pay",
                "Sara (Frontend)" to "@sara_ux",
                "Leo (Pitch Lead)" to "@leo_pitch"
            )
        )
        showToast("Created Hackathon Team Dinner Split!")
    }

    fun loadPresetCartAudit() {
        _currentTab.value = ScreenTab.SMART_CART
        addCartItem(
            name = "Studio Sound Noise Cancelling Pro",
            price = 450.00,
            requirement = "Listen to lecture recordings in my quiet bedroom.",
            category = "Audio"
        )
    }
}
