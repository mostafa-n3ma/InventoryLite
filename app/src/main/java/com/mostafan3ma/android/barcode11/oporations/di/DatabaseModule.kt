package com.mostafan3ma.android.barcode11.oporations.di

import android.content.Context
import androidx.room.Room
import com.mostafan3ma.android.barcode11.oporations.data_Mangment.localDatabaseSource.InventoryDao
import com.mostafan3ma.android.barcode11.oporations.data_Mangment.localDatabaseSource.ShopDatabase
import com.mostafan3ma.android.barcode11.oporations.data_Mangment.localDatabaseSource.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Singleton
    @Provides
    fun provideDatabase (@ApplicationContext context: Context):ShopDatabase{
        return Room.databaseBuilder(
            context,
            ShopDatabase::class.java,
            ShopDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }



    @Singleton
    @Provides
    fun provideInventoryDao(database:ShopDatabase) : InventoryDao {
        return database.inventoryDao()
    }


    @Singleton
    @Provides
    fun provideTransactionDao(database: ShopDatabase) : TransactionDao {
        return database.transactionDao()
    }






}