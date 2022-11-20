package repositories

import models.Loan
import models.LoanPayment
import models.LoanPaymentType
import javax.inject.Singleton

/**
 * LoanRepository handles both list of all "loans" made on our platform and all "loan payments" done by a user
 */
@Singleton
class LoanRepository {

    companion object {
        private val loanData = mutableListOf<Loan>()
        private val loanPaymentsData = mutableListOf<LoanPayment>()
    }

    // To get loan from id
    fun getLoan(id: String) : Loan? {
        return loanData.firstOrNull { it.id == id }
    }

    // To get loan from bankId and borrowerId
    fun getLoan(bankId: String, borrowerId: String) : Loan? {
        return loanData.firstOrNull { it.bankId==bankId && it.borrowerId==borrowerId }
    }

    // To get list of loan payments by loadId and an optional loanPaymentType filter
    fun getLoanPayments(loanId: String, loanPaymentType: LoanPaymentType? = null): List<LoanPayment> {
        return if(loanPaymentType!=null){
            loanPaymentsData.filter { it.loanId==loanId && it.paymentType == loanPaymentType }
        } else {
            loanPaymentsData.filter { it.loanId==loanId }
        }
    }

    // To register a new loan to our database
    fun addLoan(loan: Loan) {
        loanData.add(loan)
    }

    // To add a loan payment to our list of loanPayments
    fun makeLoanPayment(loanPayment: LoanPayment) {
        loanPaymentsData.add(loanPayment)
    }
}
