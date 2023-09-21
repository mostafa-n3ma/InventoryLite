package com.mostafan3ma.android.barcode11.oporations.data_Mangment.localDatabaseSource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local.Cache_transaction
import com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local.Cache_inventory

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: Cache_inventory)

    @Query("SELECT * FROM inventory")
    suspend fun getAllInventory(): List<Cache_inventory>

    // Add other queries or operations for the Inventory class
}

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Cache_transaction)

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<Cache_transaction>

    // Add other queries or operations for the Transaction class
}
