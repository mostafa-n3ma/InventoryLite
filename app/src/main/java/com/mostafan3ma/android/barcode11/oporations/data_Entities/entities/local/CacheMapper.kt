package com.mostafan3ma.android.barcode11.oporations.data_Entities.entities.local

import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Transaction
import com.mostafan3ma.android.barcode11.oporations.utils.Mapper
import javax.inject.Inject

class InventoryMapper @Inject constructor() : Mapper<Cache_inventory, Domain_Inventory> {
    override fun mapToEntity(domain: Domain_Inventory): Cache_inventory {
        return Cache_inventory(
            product_id = domain.product_id,
            product_name = domain.product_name,
            product_image = domain.product_image,
            item_purchase_price = domain.item_purchase_price,
            item_selling_price = domain.item_selling_price,
            barcode = domain.barcode,
            category = domain.category,
            description = domain.description,
            available_quantity_amount = domain.available_quantity_amount,
            total_items_quantity = domain.total_items_quantity,
            purchasing_date = domain.purchasing_date,
            expiration_date = domain.expiration_date,
            production_date = domain.production_date
        )
    }

    override fun mapFromEntity(entity: Cache_inventory): Domain_Inventory {
        return Domain_Inventory(
            product_id = entity.product_id,
            product_name = entity.product_name,
            product_image = entity.product_image,
            item_purchase_price = entity.item_purchase_price,
            item_selling_price = entity.item_selling_price,
            barcode = entity.barcode,
            category = entity.category,
            description = entity.description,
            available_quantity_amount = entity.available_quantity_amount,
            total_items_quantity = entity.total_items_quantity,
            purchasing_date = entity.purchasing_date,
            expiration_date = entity.expiration_date,
            production_date = entity.production_date
        )
    }

    override fun mapEntityList(entityList: List<Cache_inventory>): List<Domain_Inventory> {
        return entityList.map { mapFromEntity(it) }
    }
}


class TransactionMapper @Inject constructor(): Mapper<Cache_transaction, Domain_Transaction> {
    override fun mapToEntity(domain: Domain_Transaction): Cache_transaction {
        return Cache_transaction(
            transaction_id = domain.transaction_id,
            product_id = domain.product_id,
            product_name = domain.product_name,
            product_image = domain.product_image,
            item_purchase_price = domain.item_purchase_price,
            item_selling_price = domain.item_selling_price,
            barcode = domain.barcode,
            category = domain.category,
            description = domain.description,
            expiration_date = domain.expiration_date,
            production_date = domain.production_date,
            transaction_quentity = domain.transaction_quentity,
            transaction_amount = domain.transaction_amount,
            transaction_type = domain.transaction_type,
            transaction_date = domain.transaction_date,
            receipt_session = domain.receipt_session
        )
    }

    override fun mapFromEntity(entity: Cache_transaction): Domain_Transaction {
        return Domain_Transaction(
            transaction_id = entity.transaction_id,
            product_id = entity.product_id,
            product_name = entity.product_name,
            product_image = entity.product_image,
            item_purchase_price = entity.item_purchase_price,
            item_selling_price = entity.item_selling_price,
            barcode = entity.barcode,
            category = entity.category,
            description = entity.description,
            expiration_date = entity.expiration_date,
            production_date = entity.production_date,
            transaction_quentity = entity.transaction_quentity,
            transaction_amount = entity.transaction_amount,
            transaction_type = entity.transaction_type,
            transaction_date = entity.transaction_date,
            receipt_session = entity.receipt_session
        )
    }

    override fun mapEntityList(entityList: List<Cache_transaction>): List<Domain_Transaction> {
        return entityList.map { mapFromEntity(it) }
    }

}