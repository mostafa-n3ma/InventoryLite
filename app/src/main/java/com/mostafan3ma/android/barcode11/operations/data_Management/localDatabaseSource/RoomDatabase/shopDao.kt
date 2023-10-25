package com.mostafan3ma.android.barcode11.operations.data_Management.localDatabaseSource

import androidx.room.*
import com.mostafan3ma.android.barcode11.operations.data_Entities.entities.local.Cache_transaction
import com.mostafan3ma.android.barcode11.operations.data_Entities.entities.local.Cache_inventory

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: Cache_inventory):Long

    @Query("SELECT * FROM inventory")
    suspend fun getAllInventories(): List<Cache_inventory>

    @Delete
    suspend fun deleteInventory(cacheInventory: Cache_inventory): Int


    @Query("DELETE FROM inventory")
    suspend fun clearInventories()


    @Update
    suspend fun updateInventory(cacheInventory: Cache_inventory)
}

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Cache_transaction):Long

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<Cache_transaction>

    @Delete
    suspend fun deleteTransaction(cacheTransaction: Cache_transaction):Int


    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()


    @Update
    suspend fun updateTransaction(cacheTransaction: Cache_transaction)

}
