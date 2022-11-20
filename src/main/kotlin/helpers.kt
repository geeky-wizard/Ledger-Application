import com.google.inject.Guice
import com.google.inject.Injector
import managers.BankManager
import models.*
import java.time.Instant
import java.util.*

/**
 * These helper methods are used to create Bank,Borrower,Loan and LoanPayment objects
 */

fun createBank(name: String): Bank {
    return Bank(
        id =  UUID.randomUUID().toString(),
        name = name,
    )
}

fun createBorrower(name: String): Borrower {
    return Borrower(
        id =  UUID.randomUUID().toString(),
        name = name,
        createdAt = Instant.now().toEpochMilli()
    )
}

fun createLoan(bankId: String, borrowerId: String, principal: Int, term: Int, rate: Double): Loan {
    return Loan(
        id =  UUID.randomUUID().toString(),
        bankId = bankId,
        borrowerId = borrowerId,
        principal = principal,
        term = term,
        rate = rate,
        createdAt = Instant.now().toEpochMilli()
    )
}

fun createLoanPayment(loanId: String, amount: Int, month: Int, paymentType: LoanPaymentType, paymentMode: PaymentMode, utrNumber: String) : LoanPayment {
    return LoanPayment(
        id = UUID.randomUUID().toString(),
        loanId = loanId,
        amount = amount,
        month = month,
        paymentType = paymentType,
        paymentMode = paymentMode,
        utrNumber = utrNumber,
        createdAt = Instant.now().toEpochMilli()
    )
}
