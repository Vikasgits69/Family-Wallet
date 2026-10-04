package com.example.data.local

import kotlinx.coroutines.flow.Flow

class VaultRepository(private val dao: FamilyWalletDao) {

    // Reactive streams
    val allMembers: Flow<List<FamilyMemberEntity>> = dao.getAllMembers()
    val allCreditCards: Flow<List<CreditCardEntity>> = dao.getAllCreditCards()
    val allDebitCards: Flow<List<DebitCardEntity>> = dao.getAllDebitCards()
    val allBankAccounts: Flow<List<BankAccountEntity>> = dao.getAllBankAccounts()
    val allWalletsAndGiftCards: Flow<List<WalletOrGiftCardEntity>> = dao.getAllWalletsAndGiftCards()

    // Family Member CRUD
    suspend fun insertMember(member: FamilyMemberEntity) = dao.insertMember(member)
    suspend fun updateMember(member: FamilyMemberEntity) = dao.updateMember(member)
    suspend fun deleteMember(memberId: String) {
        dao.deleteMemberById(memberId)
        // Cleanly unassign any assets linked to deleted member
        dao.unassignCreditCardsForMember(memberId)
        dao.unassignDebitCardsForMember(memberId)
        dao.unassignBankAccountsForMember(memberId)
        dao.unassignWalletsAndGiftCardsForMember(memberId)
    }

    // Credit Card CRUD
    suspend fun insertCreditCard(card: CreditCardEntity) = dao.insertCreditCard(card)
    suspend fun updateCreditCard(card: CreditCardEntity) = dao.updateCreditCard(card)
    suspend fun deleteCreditCard(cardId: String) = dao.deleteCreditCardById(cardId)

    // Debit Card CRUD
    suspend fun insertDebitCard(card: DebitCardEntity) = dao.insertDebitCard(card)
    suspend fun updateDebitCard(card: DebitCardEntity) = dao.updateDebitCard(card)
    suspend fun deleteDebitCard(cardId: String) = dao.deleteDebitCardById(cardId)

    // Bank Account CRUD
    suspend fun insertBankAccount(account: BankAccountEntity) = dao.insertBankAccount(account)
    suspend fun updateBankAccount(account: BankAccountEntity) = dao.updateBankAccount(account)
    suspend fun deleteBankAccount(accountId: String) = dao.deleteBankAccountById(accountId)

    // Wallet & Gift Card CRUD
    suspend fun insertWalletOrGiftCard(item: WalletOrGiftCardEntity) = dao.insertWalletOrGiftCard(item)
    suspend fun updateWalletOrGiftCard(item: WalletOrGiftCardEntity) = dao.updateWalletOrGiftCard(item)
    suspend fun deleteWalletOrGiftCard(itemId: String) = dao.deleteWalletOrGiftCardById(itemId)
}
