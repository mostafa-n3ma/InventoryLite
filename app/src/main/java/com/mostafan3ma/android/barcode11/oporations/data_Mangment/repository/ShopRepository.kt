package com.mostafan3ma.android.barcode11.oporations.data_Mangment.repository

import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Transaction
import com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local.Cache_inventory
import com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local.InventoryMapper
import com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local.TransactionMapper
import com.mostafan3ma.android.barcode11.oporations.data_Mangment.localDatabaseSource.LocalDataSource
import com.mostafan3ma.android.barcode11.oporations.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class ShopRepository
@Inject
constructor(
    private val localDataSource: LocalDataSource,
    private val inventoryMapper: InventoryMapper,
    private val transactionMapper: TransactionMapper
) : DefaultShopRepository {

    //Inventory
    override suspend fun insert_Inventory(domainInventory: Domain_Inventory): Long {
        return localDataSource.insert_Inventory(inventoryMapper.mapToEntity(domainInventory))
    }

    override suspend fun get_Inventories(): Flow<DataState<List<Domain_Inventory>>> = flow{
        emit(DataState.Loading)
        try {
           val cacheInventories: List<Cache_inventory> = localDataSource.get_Inventories()
            val domainInventories: List<Domain_Inventory> = inventoryMapper.mapEntityList(cacheInventories)
            emit(DataState.Success(domainInventories))
        }catch (e:Exception){
            emit(DataState.Error(e))
        }

    }

    override suspend fun delete_Inventory(domainInventory: Domain_Inventory): Int {
        return localDataSource.delete_Inventory(inventoryMapper.mapToEntity(domainInventory))
    }

    override suspend fun update_Inventory(domainInventory: Domain_Inventory) {
        return localDataSource.update_Inventory(inventoryMapper.mapToEntity(domainInventory))
    }


    //Transactions
    override suspend fun insert_Transaction(domainTransaction: Domain_Transaction): Long {
        return localDataSource.insert_Transaction(transactionMapper.mapToEntity(domainTransaction))
    }

    override suspend fun get_Transactions(): Flow<DataState<List<Domain_Transaction>>> = flow{
       emit(DataState.Loading)
        try {
            val cacheTransactions = localDataSource.get_Transactions()
            val domainTransactions = transactionMapper.mapEntityList(cacheTransactions)
            emit(DataState.Success(domainTransactions))
        }catch (e:Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun delete_Transaction(domainTransaction: Domain_Transaction): Int {
        return localDataSource.delete_Transaction(transactionMapper.mapToEntity(domainTransaction))
    }

    override suspend fun update_Transaction(domainTransaction: Domain_Transaction) {
        return localDataSource.update_Transaction(transactionMapper.mapToEntity(domainTransaction))
    }

    override suspend fun clear_All_Transactions() {
        return localDataSource.clear_All_Transactions()
    }


}