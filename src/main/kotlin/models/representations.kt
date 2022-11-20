package models

// Used for Loan Status Representation
data class LoanStatusV1 (
    val loanId: String,
    val amountPaid: Int,
    val numEmiLeft: Int
)
