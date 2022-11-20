package models

data class Loan (
    val id: String,
    val bankId: String,
    val borrowerId: String,
    val principal: Int,
    val term: Int,
    val rate: Double,
    val createdAt: Long,
    val metaData: Map<String,Any?>? = null
)

// LoanPayment can be as EMI or as a lumpsum payment
// Used Enums for paymentType and paymentMode
data class LoanPayment(
    val id: String,
    val loanId: String,
    val amount: Int,
    val month: Int,
    val paymentType: LoanPaymentType = LoanPaymentType.EMI,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val utrNumber: String,
    val createdAt: Long,
)

enum class LoanPaymentType {
    EMI,
    LUMPSUM
}

enum class PaymentMode {
    CASH,
    CHEQUE,
    UPI,
    DEBIT_CARD,
    CREDIT_CARD
}
