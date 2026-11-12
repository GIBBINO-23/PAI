package com.example.data.models

enum class ExpenseCategory(val displayName: String, val iconName: String) {
    DINING("Dining & Food", "Restaurant"),
    SUBSCRIPTIONS("Subscriptions", "Subscriptions"),
    UTILITIES("Utilities & Bills", "Bolt"),
    SHOPPING("Retail & Shopping", "ShoppingBag"),
    TRANSPORT("Transit & Fuel", "DirectionsCar"),
    ENTERTAINMENT("Entertainment", "Gamepad")
}

data class ExpenseItem(
    val id: String,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val merchant: String,
    val date: String,
    val isRecurring: Boolean = false
)

data class SavingTip(
    val id: String,
    val title: String,
    val description: String,
    val estimatedMonthlySavings: Double,
    val priority: String, // "High Impact", "Quick Win", "Smart Shift"
    val category: String
)

data class CartProduct(
    val id: String,
    val name: String,
    val price: Double,
    val userRequirement: String, // What problem the user is trying to solve
    val category: String,
    val aiVerdict: String = "Analyzing...", // "Essential Match", "Overkill / High Price", "Alternative Recommended", "Impulse Buy"
    val aiAnalysis: String = "",
    val suggestedAlternative: String? = null,
    val alternativePrice: Double? = null,
    val isInCart: Boolean = true
)

data class DebtItem(
    val id: String,
    val name: String,
    val totalAmount: Double,
    val remainingBalance: Double,
    val interestRate: Double, // APR %
    val minMonthlyPayment: Double,
    val dueDate: String,
    val daysUntilDue: Int,
    val isPaidThisMonth: Boolean = false
)

data class SplitMember(
    val id: String,
    val name: String,
    val paypalHandle: String,
    val shareAmount: Double,
    val isPaid: Boolean = false
)

data class SplitBill(
    val id: String,
    val title: String,
    val totalAmount: Double,
    val members: List<SplitMember>,
    val createdAt: String,
    val isSettled: Boolean = false
)

enum class RiskLevel {
    SAFE,
    CAUTION,
    HIGH_RISK_SCAM
}

data class ScamScanResult(
    val rawTarget: String,
    val targetType: String, // "UPI Intent", "PayPal Link", "Payment QR", "Unknown URL"
    val riskScore: Int, // 0 - 100
    val riskLevel: RiskLevel,
    val merchantOrEntity: String,
    val payAddress: String,
    val threatFlags: List<String>,
    val aiDetailedAnalysis: String,
    val safetyAction: String
)

data class ChatMessage(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
