package com.mostafan3ma.android.barcode11.operations.data_Management.localDatabaseSource

import com.mostafan3ma.android.barcode11.operations.data_Entities.entities.local.Cache_inventory
import com.mostafan3ma.android.barcode11.operations.data_Entities.entities.local.Cache_transaction
import javax.inject.Inject

class LocalDataSource @Inject
constructor(private val inventoryDao: InventoryDao, private val transactionDao: TransactionDao) :
    DefaultLocalDataSource {
    override suspend fun insert_Inventory(cacheInventory: Cache_inventory): Long {
        return inventoryDao.insertInventory(cacheInventory)
    }

    override suspend fun get_Inventories(): List<Cache_inventory> {
        return inventoryDao.getAllInventories()
    }

    override suspend fun delete_Inventory(cacheInventory: Cache_inventory): Int {
        return inventoryDao.deleteInventory(cacheInventory)
    }

    override suspend fun update_Inventory(cacheInventory: Cache_inventory) {
        return inventoryDao.updateInventory(cacheInventory)
    }

    override suspend fun cleare_All_Inventories() {
        return inventoryDao.clearInventories()
    }


    override suspend fun insert_Transaction(cacheTransaction: Cache_transaction): Long {
        return transactionDao.insertTransaction(cacheTransaction)
    }

    override suspend fun get_Transactions(): List<Cache_transaction> {
        return transactionDao.getAllTransactions()
    }

    override suspend fun delete_Transaction(cacheTransaction: Cache_transaction): Int {
        return transactionDao.deleteTransaction(cacheTransaction)
    }

    override suspend fun update_Transaction(cacheTransaction: Cache_transaction) {
        return transactionDao.updateTransaction(cacheTransaction)
    }

    override suspend fun clear_All_Transactions() {
        return transactionDao.clearTransactions()
    }
}