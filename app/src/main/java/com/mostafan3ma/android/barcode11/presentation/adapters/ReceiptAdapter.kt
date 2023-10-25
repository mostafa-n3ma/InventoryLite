package com.mostafan3ma.android.barcode11.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.barcode11.databinding.ListItemReceiptScreenBinding
import com.mostafan3ma.android.barcode11.operations.data_Entities.Domain_Inventory

class ReceiptAdapter(private val listener: ReceiptListener) :
    ListAdapter<Domain_Inventory,ReceiptViewHolder>(ReceiptDiffCallBack()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        return ReceiptViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val productItem = getItem(position)
        holder.bind(productItem,listener,position)
    }
}


class ReceiptDiffCallBack : DiffUtil.ItemCallback<Domain_Inventory>() {
    override fun areItemsTheSame(oldItem: Domain_Inventory, newItem: Domain_Inventory): Boolean {
        return oldItem.total_items_quantity == newItem.total_items_quantity
    }

    override fun areContentsTheSame(oldItem: Domain_Inventory, newItem: Domain_Inventory): Boolean {
        return oldItem.total_items_quantity == newItem.total_items_quantity
    }

}
class ReceiptViewHolder(private val binding: ListItemReceiptScreenBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Domain_Inventory, listener: ReceiptListener, position: Int) {
        binding.product = item
        binding.plusBtn.setOnClickListener {
            listener.onClick(item.product_id,position,Operation.PULSE)
        }
        binding.minusBtn.setOnClickListener {
            listener.onClick(item.product_id,position,Operation.MINUS)
        }
    }

    companion object {
        fun from(parent: ViewGroup): ReceiptViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemReceiptScreenBinding.inflate(layoutInflater, parent, false)
            return ReceiptViewHolder(binding)
        }
    }
}


class ReceiptListener(val listener: (id: Int,position:Int,operation:Operation) -> Unit) {
    fun onClick(id: Int,position:Int,operation:Operation) = listener(id, position, operation)
}
enum class Operation(){PULSE,MINUS}