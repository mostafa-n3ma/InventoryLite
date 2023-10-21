package com.mostafan3ma.android.barcode11.oporations.data_Mangment.localDatabaseSource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local.Cache_transaction
import com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local.Cache_inventory

@Database(entities = [Cache_inventory::class, Cache_transaction::class], version = 2)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val DATABASE_NAME = "ShopDatabase"
    }
}
