import com.google.inject.Guice
import com.google.inject.Injector
import managers.BankManager
import models.LoanPaymentType
import java.io.BufferedReader
import java.io.FileReader
import javax.inject.Inject

/**
 * Ledger is the Main Application class
 * Each Line from Input is a Ledger Entry
 *
 * @author Rohit Bohra
 */
class Ledger @Inject constructor(
    private val bankManager: BankManager
) {

    fun run(){

        try {
            print("Enter the test file path :: ")
            val inputFilePath = readLine()!!

            val inputFile = FileReader(inputFilePath)

            val reader = BufferedReader(inputFile)

            var ledgerEntryLine: String? = null
            while (reader.readLine().also { ledgerEntryLine = it } != null) {

                // Reading each entry(or line) in the ledger and processing commands through a switch-case
                try {
                    val ledgerEntry = ledgerEntryLine!!.split(" ").toTypedArray()
                    val bankName = ledgerEntry[1]
                    val borrowerName = ledgerEntry[2]

                    // Switch-Case to determine which command needs to be processed = [LOAN, PAYMENT, BALANCE]
                    when (ledgerEntry[0]) {
                        "LOAN" -> {
                            // Assuming principal, term and rate would be valid inputs
                            val principal = ledgerEntry[3].toInt()
                            val term = ledgerEntry[4].toInt()
                            val rate = ledgerEntry[5].toDouble()

                            bankManager.addLoan(bankName, borrowerName, principal, term, rate)
                        }
                        "PAYMENT" -> {
                            // Assuming amount>=0 and month>=0
                            val amount = ledgerEntry[3].toInt()
                            val month = ledgerEntry[4].toInt()

                            bankManager.makeLoanPayment(bankName, borrowerName, amount, month, LoanPaymentType.LUMPSUM)
                        }
                        "BALANCE" -> {
                            val month = ledgerEntry[3].toInt()

                            // OUTPUT_FORMAT : BANK_NAME BORROWER_NAME AMOUNT_PAID NO_OF_EMIS_LEFT
                            val loanStatus = bankManager.getLoanStatus(bankName, borrowerName, month)

                            // Only if we get a valid loan status
                            if(loanStatus!=null){
                                println(String.format("%s %s %d %d", bankName, borrowerName, loanStatus.amountPaid, loanStatus.numEmiLeft))
                            }
                        }
                        else -> {
                            throw IllegalArgumentException("Invalid command in ledger input")
                        }
                    }
                } catch (e: Exception) {
                    print("Couldn't process ledger input::" + e.cause + "::" + e.message)
                }
            }

        } catch (e: Exception) {
            println("ERROR::Ledger Input couldn't be processed")
        }
    }
}

// We can run our program through this function
fun main(args: Array<String>) {
    val injector: Injector = Guice.createInjector(LedgerModule())
    val app: Ledger = injector.getInstance(Ledger::class.java)
    app.run()
}
