package managers

import createBorrower
import models.Borrower
import repositories.BorrowerRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BorrowerManager is designed to handle CRUD operations on Borrowers Data
 */
@Singleton
class BorrowerManager @Inject constructor(private val borrowerRepository: BorrowerRepository) {

    // To get Borrower object by id
    fun getBorrower(id: String) : Borrower? {
        return borrowerRepository.getBorrower(id)
    }

    // To get Borrower object by name
    fun getBorrowerByName(name: String) : Borrower? {
        return borrowerRepository.getBorrowerByName(name)
    }

    // To create Borrower from name
    fun addBorrower(name: String): Borrower {
        val borrower = createBorrower(name)
        borrowerRepository.addBorrower(borrower = borrower)
        return borrower
    }
}
