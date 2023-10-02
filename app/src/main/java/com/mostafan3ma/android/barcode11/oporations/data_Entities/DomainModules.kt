package com.mostafan3ma.android.barcode11.oporations.data_Entities

data class Domain_Inventory(
    var product_id: Int = 0,
    var product_name: String = "",
    var product_image: String = "",
    var packaging_purchase_price: Double = 0.0,
    var packaging_selling_price: Double = 0.0,
    var item_purchase_price: Double = 0.0,
    var item_selling_price: Double = 0.0,
    var barcode: String = "",
    var category: String = "",
    var description: String = "",
    var packaging: Int = 1,
    var available_quantity_amount: Double = 0.0,
    var total_package_quantity: Int = 0,
    var total_items_quantity: Int = 0,
    var purchasing_date: String = "",
    var expiration_date: String = "",
    var production_date: String = ""
)



data class Domain_Transaction (
    var transaction_id: Int?=null,
    var product_id: Int,
    var product_name: String,
    var product_image: String,
    var packaging_purchase_price: Double,
    var packaging_selling_price: Double,
    var item_purchase_price: Double,
    var item_selling_price: Double,
    var barcode: String,
    var category: String,
    var description: String,
    var packaging: Int,
    var expiration_date: String,
    var production_date: String,
    var transaction_quentity: Int,
    var transaction_amount: Double,
    var transaction_type: String,
    var transaction_date: String,
    var receipt_session: String
        )