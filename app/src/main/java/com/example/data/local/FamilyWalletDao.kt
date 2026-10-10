package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyWalletDao {

    // Family Members
    @Query("SELECT * FROM family_members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: FamilyMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<FamilyMemberEntity>)

    @Update
    suspend fun updateMember(member: FamilyMemberEntity)

    @Delete
    suspend fun deleteMember(member: FamilyMemberEntity)

    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteMemberById(id: String)

    @Query("DELETE FROM family_members")
    suspend fun deleteAllMembers()

    // Credit Cards
    @Query("SELECT * FROM credit_cards ORDER BY cardName ASC")
    fun getAllCreditCards(): Flow<List<CreditCardEntity>>

    @Query("SELECT * FROM credit_cards ORDER BY cardName ASC")
    suspend fun getAllCreditCardsList(): List<CreditCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditCard(card: CreditCardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditCards(cards: List<CreditCardEntity>)

    @Update
    suspend fun updateCreditCard(card: CreditCardEntity)

    @Delete
    suspend fun deleteCreditCard(card: CreditCardEntity)

    @Query("DELETE FROM credit_cards WHERE id = :id")
    suspend fun deleteCreditCardById(id: String)

    @Query("DELETE FROM credit_cards")
    suspend fun deleteAllCreditCards()

    @Query("UPDATE credit_cards SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignCreditCardsForMember(memberId: String)

    // Debit Cards
    @Query("SELECT * FROM debit_cards ORDER BY cardName ASC")
    fun getAllDebitCards(): Flow<List<DebitCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebitCard(card: DebitCardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebitCards(cards: List<DebitCardEntity>)

    @Update
    suspend fun updateDebitCard(card: DebitCardEntity)

    @Delete
    suspend fun deleteDebitCard(card: DebitCardEntity)

    @Query("DELETE FROM debit_cards WHERE id = :id")
    suspend fun deleteDebitCardById(id: String)

    @Query("DELETE FROM debit_cards")
    suspend fun deleteAllDebitCards()

    @Query("UPDATE debit_cards SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignDebitCardsForMember(memberId: String)

    // Bank Accounts
    @Query("SELECT * FROM bank_accounts ORDER BY bankName ASC")
    fun getAllBankAccounts(): Flow<List<BankAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccounts(accounts: List<BankAccountEntity>)

    @Update
    suspend fun updateBankAccount(account: BankAccountEntity)

    @Delete
    suspend fun deleteBankAccount(account: BankAccountEntity)

    @Query("DELETE FROM bank_accounts WHERE id = :id")
    suspend fun deleteBankAccountById(id: String)

    @Query("DELETE FROM bank_accounts")
    suspend fun deleteAllBankAccounts()

    @Query("UPDATE bank_accounts SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignBankAccountsForMember(memberId: String)

    // Wallets and Gift Cards
    @Query("SELECT * FROM wallets_and_gift_cards ORDER BY providerOrName ASC")
    fun getAllWalletsAndGiftCards(): Flow<List<WalletOrGiftCardEntity>>

    @Query("SELECT * FROM wallets_and_gift_cards ORDER BY providerOrName ASC")
    suspend fun getAllWalletsAndGiftCardsList(): List<WalletOrGiftCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletOrGiftCard(item: WalletOrGiftCardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletsAndGiftCards(items: List<WalletOrGiftCardEntity>)

    @Update
    suspend fun updateWalletOrGiftCard(item: WalletOrGiftCardEntity)

    @Delete
    suspend fun deleteWalletOrGiftCard(item: WalletOrGiftCardEntity)

    @Query("DELETE FROM wallets_and_gift_cards WHERE id = :id")
    suspend fun deleteWalletOrGiftCardById(id: String)

    @Query("DELETE FROM wallets_and_gift_cards")
    suspend fun deleteAllWalletsAndGiftCards()

    @Query("UPDATE wallets_and_gift_cards SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignWalletsAndGiftCardsForMember(memberId: String)

    // Documents (AADHAAR, PAN, PASSPORT, DRIVING_LICENSE, VOTER_ID, INSURANCE_POLICY, VEHICLE_RC, PROPERTY, OTHER)
    @Query("SELECT * FROM documents ORDER BY title ASC, docNumber ASC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents ORDER BY title ASC, docNumber ASC")
    suspend fun getAllDocumentsList(): List<DocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<DocumentEntity>)

    @Update
    suspend fun updateDocument(doc: DocumentEntity)

    @Delete
    suspend fun deleteDocument(doc: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: String)

    @Query("DELETE FROM documents")
    suspend fun deleteAllDocuments()

    @Query("UPDATE documents SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignDocumentsForMember(memberId: String)

    // Subscriptions
    @Query("SELECT * FROM subscriptions ORDER BY name ASC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(sub: SubscriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptions(subs: List<SubscriptionEntity>)

    @Update
    suspend fun updateSubscription(sub: SubscriptionEntity)

    @Delete
    suspend fun deleteSubscription(sub: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscriptionById(id: String)

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAllSubscriptions()

    @Query("UPDATE subscriptions SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignSubscriptionsForMember(memberId: String)

    @Transaction
    suspend fun overwriteAllData(
        members: List<FamilyMemberEntity>,
        creditCards: List<CreditCardEntity>,
        debitCards: List<DebitCardEntity>,
        bankAccounts: List<BankAccountEntity>,
        walletsAndGiftCards: List<WalletOrGiftCardEntity>,
        documents: List<DocumentEntity>,
        subscriptions: List<SubscriptionEntity> = emptyList()
    ) {
        deleteAllMembers()
        deleteAllCreditCards()
        deleteAllDebitCards()
        deleteAllBankAccounts()
        deleteAllWalletsAndGiftCards()
        deleteAllDocuments()
        deleteAllSubscriptions()

        insertMembers(members)
        insertCreditCards(creditCards)
        insertDebitCards(debitCards)
        insertBankAccounts(bankAccounts)
        insertWalletsAndGiftCards(walletsAndGiftCards)
        insertDocuments(documents)
        if (subscriptions.isNotEmpty()) {
            insertSubscriptions(subscriptions)
        }
    }
}
