package com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class Cache_inventory(
    @PrimaryKey(autoGenerate = true)
    val product_id: Int,
    val product_name: String,
    val product_image: String,
    val packaging_purchase_price: Double,
    val packaging_selling_price: Double,
    val item_purchase_price: Double,
    val item_selling_price: Double,
    val barcode: String,
    val category: String,
    val description: String,
    val packaging: Int,
    val available_quantity_amount: Double,
    val total_package_quantity: Int,
    val total_items_quantity: Int,
    val purchasing_date: String,
    val expiration_date: String,
    val production_date: String
)

@Entity(tableName = "transactions")
data class Cache_transaction(
    @PrimaryKey(autoGenerate = true)
    val transaction_id: Int?,
    val product_id: Int,
    val product_name: String,
    val product_image: String,
    val packaging_purchase_price: Double,
    val packaging_selling_price: Double,
    val item_purchase_price: Double,
    val item_selling_price: Double,
    val barcode: String,
    val category: String,
    val description: String,
    val packaging: Int,
    val expiration_date: String,
    val production_date: String,
    val transaction_quentity: Int,
    val transaction_amount: Double,
    val transaction_type: String,
    val transaction_date: String,
    val receipt_session: String
)
