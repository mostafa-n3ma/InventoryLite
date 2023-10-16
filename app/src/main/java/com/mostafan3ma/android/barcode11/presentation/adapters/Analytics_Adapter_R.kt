package com.mostafan3ma.android.barcode11.presentation.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.barcode11.databinding.ListItemReciptsBinding
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Transaction
import com.mostafan3ma.android.barcode11.oporations.utils.getValueInDP
import java.lang.StringBuilder

class Analytics_Adapter_R : ListAdapter<ReceiptItem,ReceiptsViewHolder>(ReceiptItemsViewHolder()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptsViewHolder {
        return ReceiptsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReceiptsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class ReceiptsViewHolder (private val binding:ListItemReciptsBinding) :RecyclerView.ViewHolder(binding.root) {
    fun bind (item: ReceiptItem){
        binding.receiptItem = item
        val items_List = item.items_List
        val sb:StringBuilder = StringBuilder()
        var transactions_total_amount = 0.0
        items_List.map { domainTransaction ->
            sb.append("${domainTransaction.product_name} X ${domainTransaction.transaction_quentity} \n")
            transactions_total_amount+=domainTransaction.transaction_amount
        }
        binding.itemProductId.text = item.receipt_id
        binding.itemReciptList.text = sb.toString()
        binding.itemReceiptAmount.text = transactions_total_amount.toString()

        binding.itemCard.setOnClickListener {
            checkCardExpendableStatus(binding,it.context)
        }


    }

    private fun checkCardExpendableStatus(binding: ListItemReciptsBinding, context: Context) {
        if (binding.itemLayout.height == 197){
            binding.itemLayout.updateLayoutParams {height = WRAP_CONTENT }
        }else{
            binding.itemLayout.updateLayoutParams {height = 197 }
        }
    }

    companion object{
        const val TAG ="ReceiptsViewHolder"
        fun from(parent: ViewGroup) : ReceiptsViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemReciptsBinding.inflate(layoutInflater,parent,false)
            return ReceiptsViewHolder(binding)
        }
    }

}

class ReceiptItemsViewHolder :DiffUtil.ItemCallback<ReceiptItem>(){
    override fun areItemsTheSame(oldItem: ReceiptItem, newItem: ReceiptItem): Boolean {
        return oldItem.receipt_id == newItem.receipt_id
    }

    override fun areContentsTheSame(oldItem: ReceiptItem, newItem: ReceiptItem): Boolean {
        return oldItem==newItem
    }

}


data class ReceiptItem
    (
    var receipt_id:String = "",
    var items_List:MutableList<Domain_Transaction> = mutableListOf()
    )