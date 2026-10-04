package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyWalletDao {

    // Family Members
    @Query("SELECT * FROM family_members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: FamilyMemberEntity)

    @Update
    suspend fun updateMember(member: FamilyMemberEntity)

    @Delete
    suspend fun deleteMember(member: FamilyMemberEntity)

    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteMemberById(id: String)

    // Credit Cards
    @Query("SELECT * FROM credit_cards ORDER BY cardName ASC")
    fun getAllCreditCards(): Flow<List<CreditCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditCard(card: CreditCardEntity)

    @Update
    suspend fun updateCreditCard(card: CreditCardEntity)

    @Delete
    suspend fun deleteCreditCard(card: CreditCardEntity)

    @Query("DELETE FROM credit_cards WHERE id = :id")
    suspend fun deleteCreditCardById(id: String)

    @Query("UPDATE credit_cards SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignCreditCardsForMember(memberId: String)

    // Debit Cards
    @Query("SELECT * FROM debit_cards ORDER BY cardName ASC")
    fun getAllDebitCards(): Flow<List<DebitCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebitCard(card: DebitCardEntity)

    @Update
    suspend fun updateDebitCard(card: DebitCardEntity)

    @Delete
    suspend fun deleteDebitCard(card: DebitCardEntity)

    @Query("DELETE FROM debit_cards WHERE id = :id")
    suspend fun deleteDebitCardById(id: String)

    @Query("UPDATE debit_cards SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignDebitCardsForMember(memberId: String)

    // Bank Accounts
    @Query("SELECT * FROM bank_accounts ORDER BY bankName ASC")
    fun getAllBankAccounts(): Flow<List<BankAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccountEntity)

    @Update
    suspend fun updateBankAccount(account: BankAccountEntity)

    @Delete
    suspend fun deleteBankAccount(account: BankAccountEntity)

    @Query("DELETE FROM bank_accounts WHERE id = :id")
    suspend fun deleteBankAccountById(id: String)

    @Query("UPDATE bank_accounts SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignBankAccountsForMember(memberId: String)

    // Wallets and Gift Cards
    @Query("SELECT * FROM wallets_and_gift_cards ORDER BY providerOrName ASC")
    fun getAllWalletsAndGiftCards(): Flow<List<WalletOrGiftCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletOrGiftCard(item: WalletOrGiftCardEntity)

    @Update
    suspend fun updateWalletOrGiftCard(item: WalletOrGiftCardEntity)

    @Delete
    suspend fun deleteWalletOrGiftCard(item: WalletOrGiftCardEntity)

    @Query("DELETE FROM wallets_and_gift_cards WHERE id = :id")
    suspend fun deleteWalletOrGiftCardById(id: String)

    @Query("UPDATE wallets_and_gift_cards SET memberId = '' WHERE memberId = :memberId")
    suspend fun unassignWalletsAndGiftCardsForMember(memberId: String)
}
