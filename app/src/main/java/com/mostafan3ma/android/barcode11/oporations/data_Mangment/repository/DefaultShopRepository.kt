package com.mostafan3ma.android.barcode11.oporations.data_Mangment.repository

import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Transaction
import com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local.Cache_inventory
import com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local.Cache_transaction
import com.mostafan3ma.android.barcode11.oporations.utils.DataState
import kotlinx.coroutines.flow.Flow

interface DefaultShopRepository {
     suspend fun insert_Inventory(domainInventory: Domain_Inventory): Long

     suspend fun get_Inventories(): Flow<DataState<List<Domain_Inventory>>>

     suspend fun delete_Inventory(domainInventory: Domain_Inventory): Int

     suspend fun update_Inventory(domainInventory: Domain_Inventory)




     suspend fun insert_Transaction(domainTransaction: Domain_Transaction): Long

     suspend fun get_Transactions(): Flow<DataState<List<Domain_Transaction>>>

     suspend fun delete_Transaction(domainTransaction: Domain_Transaction): Int

     suspend fun update_Transaction(domainTransaction: Domain_Transaction)

}