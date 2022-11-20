package repositories

import models.Borrower
import javax.inject.Singleton

/**
 * BorrowerRepository is to store borrowers list
 * Data can also be instead stored/retrieved from a database
 */
@Singleton
class BorrowerRepository {

    // Using a list to store list of all borrowers
    private val borrowersData = mutableListOf<Borrower>()

    // To get borrower from id
    fun getBorrower(id: String) : Borrower? {
        return borrowersData.firstOrNull { it.id == id }
    }

    // To get borrower by name
    fun getBorrowerByName(name: String) : Borrower? {
        return borrowersData.firstOrNull { it.name == name }
    }

    // To add borrower to our database
    fun addBorrower(borrower: Borrower) {
        borrowersData.add(borrower)
    }
}
