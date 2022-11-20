import com.google.inject.Guice
import com.google.inject.Injector
import managers.BankManager
import models.LoanPaymentType
import org.junit.Test
import kotlin.math.ceil

class LedgerTests {

    private val bankManager by lazy {
        val injector: Injector = Guice.createInjector(LedgerModule())
        injector.getInstance(BankManager::class.java)
    }

    @Test
    fun `Adding New Loan`() {

        val bankName = "HDFC"
        val borrowerName = "Rohit"
        val principal = 1000
        val rate = 10.0
        val term = 2

        bankManager.addLoan(
            bankName = bankName,
            borrowerName = borrowerName,
            principal = principal,
            rate = rate,
            term = term,
        )

        // making sure our bank and borrower are added to their respective db
        assert(bankManager.getBankByName(bankName)!=null) {"Bank not present in database"}
        assert(bankManager.getBorrowerByName(borrowerName)!=null) {"Borrower not present in database"}


        val amount = ceil(principal + (principal*rate*term)/100).toInt()

        // total months of emi = term*12 = 12*2 = 24
        val numEmiLeft = term*12

        // LoanStatus at month 0
        val loanStatusV1 = bankManager.getLoanStatus(bankName, borrowerName, 0)

        assert(loanStatusV1!=null) {"LoanStatus cannot be null"}
        assert(loanStatusV1?.amountPaid==0) {"At month 0, amountPaid should be 0"}
        assert(loanStatusV1?.numEmiLeft==numEmiLeft) {"At month 0, EMIs left is not correct"}

        // Checking loan status for a month>numEmiLeft
        val loanStatusV2 = bankManager.getLoanStatus(bankName, borrowerName, 30)

        assert(loanStatusV2!=null) {"LoanStatus cannot be null"}
        assert(loanStatusV2?.amountPaid==amount) {"After emi duration, total amount will be paid up"}
        assert(loanStatusV2?.numEmiLeft==0) {"After emi duration, number of emi left will be 0"}
    }

    @Test
    fun `Checking Loan Payments`() {

        val bankName = "HDFC"
        val borrowerName = "Rohit"
        val principal = 1000
        val rate = 10.0
        val term = 2

        bankManager.addLoan(
            bankName = bankName,
            borrowerName = borrowerName,
            principal = principal,
            rate = rate,
            term = term,
        )

        val amount = ceil(principal + (principal*rate*term)/100).toInt()
        val emi = ceil(amount.toDouble()/(term*12)).toInt()

        val lumpSumPayments = listOf(Pair(100,6), Pair(200,12))

        bankManager.makeLoanPayment(
            bankName = bankName,
            borrowerName = borrowerName,
            amount = lumpSumPayments[0].first,
            month = lumpSumPayments[0].second,
            loanPaymentType = LoanPaymentType.LUMPSUM
        )

        bankManager.makeLoanPayment(
            bankName = bankName,
            borrowerName = borrowerName,
            amount = lumpSumPayments[1].first,
            month = lumpSumPayments[1].second,
            loanPaymentType = LoanPaymentType.LUMPSUM
        )

        val loanStatusV1 = bankManager.getLoanStatus(bankName, borrowerName, 3)
        val lumpSumPaymentV1 = lumpSumPayments.filter { it.second<=0 }.sumOf { it.first }
        assert(loanStatusV1?.amountPaid==emi*3+lumpSumPaymentV1) {"Amount Paid by EMIs in 3 months is incorrect"}

        val loanStatusV2 = bankManager.getLoanStatus(bankName, borrowerName, 7)
        val lumpSumPaymentV2 = lumpSumPayments.filter { it.second<=7 }.sumOf { it.first }
        assert(loanStatusV2?.amountPaid==(emi*7+lumpSumPaymentV2)) {"Amount Paid by EMIs in 7 months is incorrect"}

        val loanStatusV3 = bankManager.getLoanStatus(bankName, borrowerName, 12)
        val lumpSumPaymentV3 = lumpSumPayments.filter { it.second<=12 }.sumOf { it.first }
        assert(loanStatusV3?.amountPaid==(emi*12+lumpSumPaymentV3)) {"Amount Paid by EMIs in 12 months is incorrect"}
    }
}
