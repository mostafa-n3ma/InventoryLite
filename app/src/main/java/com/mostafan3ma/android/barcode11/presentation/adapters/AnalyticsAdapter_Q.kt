package com.mostafan3ma.android.barcode11.presentation.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.barcode11.R
import com.mostafan3ma.android.barcode11.databinding.ListItemAnalyticsQuantitiesBinding
import com.mostafan3ma.android.barcode11.operations.data_Entities.Domain_Inventory

class AnalyticsAdapter_Q (private val context:Context):ListAdapter<Domain_Inventory,QuantitiesViewHolder>(AnalyticsDiffCallBack_Q()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuantitiesViewHolder {
        return QuantitiesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: QuantitiesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,context)
    }
}

class AnalyticsDiffCallBack_Q : DiffUtil.ItemCallback<Domain_Inventory>() {
    override fun areItemsTheSame(oldItem: Domain_Inventory, newItem: Domain_Inventory): Boolean {
        return oldItem.product_id == newItem.product_id
    }

    override fun areContentsTheSame(oldItem: Domain_Inventory, newItem: Domain_Inventory): Boolean {
        return oldItem == newItem
    }

}

class QuantitiesViewHolder ( private val binding: ListItemAnalyticsQuantitiesBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Domain_Inventory, context: Context){
        binding.product = item
        binding.itemTotalQuantity.text = "${item.total_items_quantity} Pc"

        if(item.total_items_quantity <= 10.0){
            Log.d(TAG, "bind: item ${item.product_name} quantity : ${item.total_items_quantity}")
            binding.itemLayout.setBackgroundColor(context.getColor(R.color.red_card))
        }
    }
    companion object{
        const val TAG = "QuantitiesViewHolder"
        fun from(parent: ViewGroup):QuantitiesViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemAnalyticsQuantitiesBinding.inflate(layoutInflater,parent,false)
            return QuantitiesViewHolder(binding)
        }
    }

}





