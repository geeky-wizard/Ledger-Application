package managers

import createBank
import createLoan
import createLoanPayment
import models.*
import repositories.BankRepository
import repositories.LoanRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 BankManager is injected with bankRepository, loanRepository and borrowerManager
 All CRUD operations for our platform are through this manager
 **/
@Singleton
class BankManager @Inject constructor(
    private val bankRepository: BankRepository,
    private val loanRepository: LoanRepository,
    private val borrowerManager: BorrowerManager
){

    // Note :: All values are rounded up using ceil function wherever necessary as per the assumptions

    // To calculate amount by formula : A = CEIL(P + (P*R*T)/100) where P=Principal,R=Rate(% per annum) and T=Term(in years)
    private fun calculateAmount(loan: Loan) : Int {
        return ceil(loan.principal + (loan.principal * loan.term * loan.rate / 100)).toInt()
    }

    // To calculate emi amount by formula : EMI = CEIL(Amount / (T*12)) where T=Term(in years)
    private fun calculateEMIAmount(loan: Loan) : Int {
        return ceil(calculateAmount(loan).toDouble() / (loan.term*12)).toInt()
    }

    // To calculate the number of emi remaining by formula : EMIs Left = CEIL(Amount Remaining / EMI)
    private fun getNumberOfEmiLeft(amountRemaining: Int, emiAmount: Int): Int {
        return ceil(amountRemaining.toDouble() / emiAmount).toInt()
    }

    // To get Bank by bank name
    fun getBankByName(bankName: String) : Bank? {
        return bankRepository.getBankByName(bankName)
    }

    // To get Borrower by name
    fun getBorrowerByName(name: String) : Borrower? {
        return borrowerManager.getBorrowerByName(name)
    }

    // To get Loan Status - loanId, amount paid till now and number of emi left
    fun getLoanStatus(bankName: String, borrowerName: String, month: Int) : LoanStatusV1? {

        try {
            val bankId = bankRepository.getBankByName(bankName)?.id ?: throw NullPointerException("Bank doesn't exist")
            val borrowerId = borrowerManager.getBorrowerByName(borrowerName)?.id ?: throw NullPointerException("Borrower doesn't exist")

            val loan = loanRepository.getLoan(bankId, borrowerId) ?: throw NullPointerException("Loan doesn't exist for $bankId and $borrowerId")

            // Fetching lumpSum Amount Paid till now
            val lumpSumAmountPaid = loanRepository.getLoanPayments(loan.id, LoanPaymentType.LUMPSUM)
                .filter{ it.month<=month }
                .sumOf { it.amount }

            /*
                We're calculating emi and not using getLoanPayments method because we take the assumption that
                "All EMI are paid up until the Month given"
            */
            val emi = calculateEMIAmount(loan)
            val emiAmountPaid = emi * month

            // Also handling a case where month>EMIs
            // AmountPaid cannot exceed the total amount

            val amountPaid = lumpSumAmountPaid+emiAmountPaid
            val totalAmountToBePaid = calculateAmount(loan)

            // Remaining amount = totalAmountToBePaid - amountPaid
            val numEmiLeft = getNumberOfEmiLeft(totalAmountToBePaid - amountPaid, emi)

            return LoanStatusV1(loan.id, min(amountPaid,totalAmountToBePaid), max(numEmiLeft,0))
        } catch (e: Exception) {
            println("ERROR::LoanStatus couldn't be retrieved::${e.message}")
            return null
        }

    }

    private fun addBank(bankName: String) : Bank {
        // Creating bank object using bankName and adding to our db
        val bank = createBank(bankName)
        bankRepository.addBank(bank)
        return bank
    }

    fun addLoan(bankName: String, borrowerName: String, principal: Int, term: Int, rate: Double) {

        // Add bank to our db if not present
        var bank = bankRepository.getBankByName(bankName)
        if(bank==null){
            bank = addBank(bankName)
        }

        // Add borrower to our db if not present
        var borrower = borrowerManager.getBorrowerByName(borrowerName)
        if(borrower==null){
            borrower = borrowerManager.addBorrower(borrowerName)
        }

        // Creating Loan object using bankId, borrowerId, principal, term and rate
        val loan = createLoan(
            bankId = bank.id,
            borrowerId = borrower.id,
            principal = principal,
            term = term,
            rate = rate
        )

        // Adding loan to our db
        loanRepository.addLoan(loan);
    }

    fun makeLoanPayment(bankName: String, borrowerName: String, amount: Int, month: Int, loanPaymentType: LoanPaymentType) {

        try {
            val bankId = bankRepository.getBankByName(bankName)?.id ?: throw NullPointerException("Bank doesn't exist")
            val borrowerId = borrowerManager.getBorrowerByName(borrowerName)?.id ?: throw NullPointerException("Borrower doesn't exist")

            val loan = loanRepository.getLoan(bankId, borrowerId) ?: throw NullPointerException("Loan doesn't exist for $bankId and $borrowerId")

            // paymentMode and utrNumber are just dummy values
            val loanPayment = createLoanPayment(
                loanId = loan.id,
                amount = amount,
                month = month,
                paymentType = loanPaymentType,
                paymentMode = PaymentMode.CASH,
                utrNumber = "1234567890"
            )

            // Adding loan payment info to our db
            loanRepository.makeLoanPayment(loanPayment)
        } catch (e: Exception) {
            // Couldn't make loan payment
            println("ERROR::Couldn't make Loan Payment::${e.message}")
        }
    }

}
