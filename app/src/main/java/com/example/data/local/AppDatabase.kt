package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        FamilyMemberEntity::class,
        CreditCardEntity::class,
        DebitCardEntity::class,
        BankAccountEntity::class,
        WalletOrGiftCardEntity::class,
        DocumentEntity::class,
        SubscriptionEntity::class
    ],
    version = 11,
    exportSchema = false
)
@TypeConverters(VaultTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun familyWalletDao(): FamilyWalletDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "family_wallet_vault.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
