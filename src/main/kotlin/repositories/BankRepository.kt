package repositories

import models.Bank
import javax.inject.Singleton

/**
 * BankRepository is to store banks list
 * Data can also be instead stored/retrieved from a database
 */
@Singleton
class BankRepository {

    private val banksData = mutableListOf<Bank>()

    fun getBank(id: String) : Bank? {
        return banksData.firstOrNull { it.id == id }
    }

    fun getBankByName(name: String) : Bank? {
        return banksData.firstOrNull { it.name == name }
    }

    fun addBank(bank: Bank) {
        banksData.add(bank)
    }
}
