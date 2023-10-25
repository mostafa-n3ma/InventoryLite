package com.mostafan3ma.android.barcode11.operations.data_Management.localDatabaseSource

import com.mostafan3ma.android.barcode11.operations.data_Entities.entities.local.Cache_inventory
import com.mostafan3ma.android.barcode11.operations.data_Entities.entities.local.Cache_transaction

interface DefaultLocalDataSource {
    //inventory
    suspend fun insert_Inventory(cacheInventory: Cache_inventory):Long
    suspend fun get_Inventories() : List<Cache_inventory>
    suspend fun delete_Inventory(cacheInventory: Cache_inventory) : Int
    suspend fun update_Inventory(cacheInventory: Cache_inventory)
    suspend fun cleare_All_Inventories()


    // transaction
    suspend fun insert_Transaction(cacheTransaction: Cache_transaction):Long
    suspend fun get_Transactions() : List<Cache_transaction>
    suspend fun delete_Transaction(cacheTransaction: Cache_transaction) : Int
    suspend fun update_Transaction(cacheTransaction: Cache_transaction)
    suspend fun clear_All_Transactions()







}