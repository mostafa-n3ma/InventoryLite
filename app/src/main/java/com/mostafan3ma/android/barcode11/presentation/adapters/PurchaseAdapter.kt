package com.mostafan3ma.android.barcode11.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.barcode11.databinding.ListItemPurchaseScreenBinding
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory

class PurchaseAdapter(private val PurchaseListener: PurchaseListener) :
    ListAdapter<Domain_Inventory, PurchaseAdapter.PurchaseViewHolder>(PurchaseDiffCallBack()) {


    class PurchaseViewHolder(private val binding: ListItemPurchaseScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Domain_Inventory) {
            binding.product = item
        }

        companion object {
            fun from(parent: ViewGroup): PurchaseViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemPurchaseScreenBinding.inflate(layoutInflater, parent, false)
                return PurchaseViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        return PurchaseViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PurchaseViewHolder, position: Int) {
        val productItem = getItem(position)
        holder.bind(productItem)
        holder.itemView.setOnClickListener {
            PurchaseListener.listener(position)
        }
    }
}


class PurchaseDiffCallBack : DiffUtil.ItemCallback<Domain_Inventory>() {
    override fun areItemsTheSame(oldItem: Domain_Inventory, newItem: Domain_Inventory): Boolean {
        return oldItem.barcode == newItem.barcode
    }

    override fun areContentsTheSame(oldItem: Domain_Inventory, newItem: Domain_Inventory): Boolean {
        return oldItem == newItem
    }

}

class PurchaseListener(val listener: (position: Int) -> Unit) {
    fun onClick(item: Domain_Inventory, position: Int) = listener(position)
}