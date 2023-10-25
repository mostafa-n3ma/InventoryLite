package com.mostafan3ma.android.barcode11.operations.di

import com.mostafan3ma.android.barcode11.operations.data_Entities.entities.local.InventoryMapper
import com.mostafan3ma.android.barcode11.operations.data_Entities.entities.local.TransactionMapper
import com.mostafan3ma.android.barcode11.operations.data_Management.localDatabaseSource.LocalDataSource
import com.mostafan3ma.android.barcode11.operations.data_Management.repository.ShopRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        localDataSource: LocalDataSource, inventoryMapper: InventoryMapper,
        transactionMapper: TransactionMapper
    ): ShopRepository {
        return ShopRepository(localDataSource, inventoryMapper, transactionMapper)
    }


}