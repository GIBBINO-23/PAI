package com.example.data.repository

import com.example.data.models.CartProduct
import com.example.data.models.ChatMessage
import com.example.data.models.DebtItem
import com.example.data.models.ExpenseCategory
import com.example.data.models.ExpenseItem
import com.example.data.models.SavingTip
import com.example.data.models.ScamScanResult
import com.example.data.models.SplitBill
import com.example.data.models.SplitMember
import com.example.data.service.GeminiAiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class PayWiseRepository(
    private val aiService: GeminiAiService = GeminiAiService()
) {
    // Current user balance in PayPal
    private val _payPalBalance = MutableStateFlow(2480.50)
    val payPalBalance: StateFlow<Double> = _payPalBalance.asStateFlow()

    // Expenses list
    private val _expenses = MutableStateFlow<List<ExpenseItem>>(getInitialExpenses())
    val expenses: StateFlow<List<ExpenseItem>> = _expenses.asStateFlow()

    // Smart Shopping Cart
    private val _cartItems = MutableStateFlow<List<CartProduct>>(getInitialCart())
    val cartItems: StateFlow<List<CartProduct>> = _cartItems.asStateFlow()

    // AI Saving Tips
    private val _savingTips = MutableStateFlow<List<SavingTip>>(emptyList())
    val savingTips: StateFlow<List<SavingTip>> = _savingTips.asStateFlow()

    // Debts & Loans
    private val _debts = MutableStateFlow<List<DebtItem>>(getInitialDebts())
    val debts: StateFlow<List<DebtItem>> = _debts.asStateFlow()

    // Split Bills
    private val _splitBills = MutableStateFlow<List<SplitBill>>(getInitialSplitBills())
    val splitBills: StateFlow<List<SplitBill>> = _splitBills.asStateFlow()

    // Chatbot Messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(getInitialChat())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Recent Scam Scans
    private val _recentScans = MutableStateFlow<List<ScamScanResult>>(emptyList())
    val recentScans: StateFlow<List<ScamScanResult>> = _recentScans.asStateFlow()

    // Financial Health Score (0 - 100)
    val healthScore: Int
        get() {
            val totalExpense = _expenses.value.sumOf { it.amount }
            val debtTotal = _debts.value.sumOf { it.remainingBalance }
            val balance = _payPalBalance.value
            return ((balance / (totalExpense + (debtTotal * 0.05))) * 65).toInt().coerceIn(35, 96)
        }

    // Expense Management
    fun addExpense(title: String, amount: Double, category: ExpenseCategory, merchant: String, recurring: Boolean) {
        val newItem = ExpenseItem(
            id = UUID.randomUUID().toString(),
            title = title,
            amount = amount,
            category = category,
            merchant = merchant,
            date = "Today",
            isRecurring = recurring
        )
        _expenses.value = listOf(newItem) + _expenses.value
        _payPalBalance.value = (_payPalBalance.value - amount).coerceAtLeast(0.0)
    }

    suspend fun refreshSavingTips() {
        val tips = aiService.generateSavingTips(_expenses.value)
        _savingTips.value = tips
    }

    // Smart Cart Management
    suspend fun addProductToCart(name: String, price: Double, requirement: String, category: String) {
        val (verdict, analysis) = aiService.auditCartRequirement(name, price, requirement, category)
        val newProduct = CartProduct(
            id = UUID.randomUUID().toString(),
            name = name,
            price = price,
            userRequirement = requirement,
            category = category,
            aiVerdict = verdict,
            aiAnalysis = analysis,
            isInCart = true
        )
        _cartItems.value = _cartItems.value + newProduct
    }

    fun removeCartItem(id: String) {
        _cartItems.value = _cartItems.value.filter { it.id != id }
    }

    fun checkoutCartWithPayPal(): Double {
        val total = _cartItems.value.filter { it.isInCart }.sumOf { it.price }
        _payPalBalance.value = (_payPalBalance.value - total).coerceAtLeast(0.0)
        _cartItems.value = emptyList()
        return total
    }

    // Split Bills
    fun createSplitBill(title: String, totalAmount: Double, members: List<Pair<String, String>>) {
        val perPerson = totalAmount / (members.size + 1)
        val splitMembers = members.map { (name, handle) ->
            SplitMember(
                id = UUID.randomUUID().toString(),
                name = name,
                paypalHandle = handle,
                shareAmount = perPerson,
                isPaid = false
            )
        }
        val bill = SplitBill(
            id = UUID.randomUUID().toString(),
            title = title,
            totalAmount = totalAmount,
            members = splitMembers,
            createdAt = "Just now",
            isSettled = false
        )
        _splitBills.value = listOf(bill) + _splitBills.value
    }

    fun markSplitMemberPaid(billId: String, memberId: String) {
        _splitBills.value = _splitBills.value.map { bill ->
            if (bill.id == billId) {
                val updatedMembers = bill.members.map { mem ->
                    if (mem.id == memberId) mem.copy(isPaid = true) else mem
                }
                bill.copy(
                    members = updatedMembers,
                    isSettled = updatedMembers.all { it.isPaid }
                )
            } else bill
        }
    }

    // Debts
    fun payDebtInstallment(debtId: String, paymentAmount: Double) {
        _debts.value = _debts.value.map { debt ->
            if (debt.id == debtId) {
                val newRemaining = (debt.remainingBalance - paymentAmount).coerceAtLeast(0.0)
                debt.copy(
                    remainingBalance = newRemaining,
                    isPaidThisMonth = true
                )
            } else debt
        }
        _payPalBalance.value = (_payPalBalance.value - paymentAmount).coerceAtLeast(0.0)
    }

    // Scam Shield Analysis
    suspend fun analyzePaymentTarget(rawInput: String, merchant: String = "", vpa: String = "", amount: Double? = null): ScamScanResult {
        // Parse raw input if it's a UPI URL
        var parsedMerchant = merchant
        var parsedVpa = vpa
        var parsedAmount = amount

        if (rawInput.startsWith("upi://", ignoreCase = true)) {
            val params = rawInput.substringAfter("?").split("&").associate {
                val parts = it.split("=")
                if (parts.size == 2) parts[0] to parts[1] else "" to ""
            }
            if (parsedVpa.isBlank()) parsedVpa = params["pa"].orEmpty()
            if (parsedMerchant.isBlank()) parsedMerchant = params["pn"]?.replace("%20", " ").orEmpty()
            if (parsedAmount == null) parsedAmount = params["am"]?.toDoubleOrNull()
        }

        val result = aiService.analyzePaymentSafety(rawInput, parsedMerchant, parsedVpa, parsedAmount)
        _recentScans.value = listOf(result) + _recentScans.value.take(4)
        return result
    }

    // Chatbot
    suspend fun sendChatMessage(userText: String): String {
        val userMsg = ChatMessage(UUID.randomUUID().toString(), isFromUser = true, text = userText)
        _chatMessages.value = _chatMessages.value + userMsg

        val contextSummary = """
            PayPal Balance: $${_payPalBalance.value}
            Monthly Expenses Total: $${_expenses.value.sumOf { it.amount }}
            Top Category: Dining & Subscriptions
            Active Debts Remaining: $${_debts.value.sumOf { it.remainingBalance }}
            Items in Cart: ${_cartItems.value.size} items totaling $${_cartItems.value.sumOf { it.price }}
        """.trimIndent()

        val aiReplyText = aiService.askChatbot(userText, contextSummary)
        val aiMsg = ChatMessage(UUID.randomUUID().toString(), isFromUser = false, text = aiReplyText)
        _chatMessages.value = _chatMessages.value + aiMsg
        return aiReplyText
    }

    private fun getInitialExpenses(): List<ExpenseItem> = listOf(
        ExpenseItem("exp_1", "Netflix Ultra HD Plan", 22.99, ExpenseCategory.SUBSCRIPTIONS, "Netflix.com", "Nov 10", isRecurring = true),
        ExpenseItem("exp_2", "Whole Foods Grocery", 142.50, ExpenseCategory.DINING, "Whole Foods Market", "Nov 9"),
        ExpenseItem("exp_3", "Spotify Duo Premium", 14.99, ExpenseCategory.SUBSCRIPTIONS, "Spotify AB", "Nov 8", isRecurring = true),
        ExpenseItem("exp_4", "Electric Utility Bill", 88.40, ExpenseCategory.UTILITIES, "City Power & Gas", "Nov 6", isRecurring = true),
        ExpenseItem("exp_5", "Blue Bottle Coffee (Weekly)", 34.00, ExpenseCategory.DINING, "Blue Bottle", "Nov 5"),
        ExpenseItem("exp_6", "Uber Ride Downtown", 28.75, ExpenseCategory.TRANSPORT, "Uber BV", "Nov 4"),
        ExpenseItem("exp_7", "Gym Membership", 55.00, ExpenseCategory.SUBSCRIPTIONS, "Equinox Fitness", "Nov 1", isRecurring = true)
    )

    private fun getInitialCart(): List<CartProduct> = listOf(
        CartProduct(
            id = "cart_1",
            name = "Bose QuietComfort 45",
            price = 329.00,
            userRequirement = "Need good noise cancelling to focus on coding in busy coffee shops.",
            category = "Electronics",
            aiVerdict = "Essential Match",
            aiAnalysis = "Active noise cancelling directly solves your focus requirement for coding environments. Best-in-class comfort for long sessions.",
            suggestedAlternative = "Sony WH-1000XM4 (Certified Refurbished)",
            alternativePrice = 219.00
        ),
        CartProduct(
            id = "cart_2",
            name = "Ergonomic Mechanical Keyboard RGB",
            price = 249.99,
            userRequirement = "Need a keyboard for writing notes and homework.",
            category = "Accessories",
            aiVerdict = "Overkill Spec",
            aiAnalysis = "For writing notes and homework, a $250 custom mechanical keyboard is excessive. A quality low-profile wireless keyboard ($45) achieves identical results, saving you $204.",
            suggestedAlternative = "Logitech Signature K650 Wireless",
            alternativePrice = 44.99
        ),
        CartProduct(
            id = "cart_3",
            name = "Stainless Steel Smart Mug Heated",
            price = 149.00,
            userRequirement = "Keep coffee warm while studying.",
            category = "Home",
            aiVerdict = "Impulse Buy Risk",
            aiAnalysis = "High luxury impulse price for reheating. A standard double-wall vacuum insulated tumbler ($22) retains heat for 6+ hours with zero battery degradation.",
            suggestedAlternative = "Hydro Flask Coffee Flask 16oz",
            alternativePrice = 27.95
        )
    )

    private fun getInitialDebts(): List<DebtItem> = listOf(
        DebtItem(
            id = "debt_1",
            name = "PayPal Credit Line",
            totalAmount = 2500.00,
            remainingBalance = 1140.00,
            interestRate = 22.9,
            minMonthlyPayment = 75.00,
            dueDate = "Nov 18, 2026",
            daysUntilDue = 6,
            isPaidThisMonth = false
        ),
        DebtItem(
            id = "debt_2",
            name = "Federal Student Loan (Subsidized)",
            totalAmount = 18000.00,
            remainingBalance = 12450.00,
            interestRate = 4.8,
            minMonthlyPayment = 165.00,
            dueDate = "Nov 25, 2026",
            daysUntilDue = 13,
            isPaidThisMonth = false
        ),
        DebtItem(
            id = "debt_3",
            name = "Auto Financing Loan",
            totalAmount = 14500.00,
            remainingBalance = 5200.00,
            interestRate = 6.2,
            minMonthlyPayment = 230.00,
            dueDate = "Dec 2, 2026",
            daysUntilDue = 20,
            isPaidThisMonth = true
        )
    )

    private fun getInitialSplitBills(): List<SplitBill> = listOf(
        SplitBill(
            id = "split_1",
            title = "Weekend AirBnb & Groceries",
            totalAmount = 480.00,
            members = listOf(
                SplitMember("m_1", "Alex Rivera", "@alex_r99", 120.00, isPaid = true),
                SplitMember("m_2", "Maya Chen", "@mayac_design", 120.00, isPaid = false),
                SplitMember("m_3", "Jordan Taylor", "@jtaylor_tech", 120.00, isPaid = false)
            ),
            createdAt = "Yesterday",
            isSettled = false
        )
    )

    private fun getInitialChat(): List<ChatMessage> = listOf(
        ChatMessage(
            id = "msg_1",
            isFromUser = false,
            text = "Welcome to PayWise AI! I'm your PayPal intelligent financial guardian. I monitor your spending patterns, analyze items in your shopping cart before you buy, protect you from QR/UPI scams, and keep your debts on track. How can I assist your finances today?"
        )
    )
}
