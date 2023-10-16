package com.mostafan3ma.android.barcode11.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.barcode11.R
import com.mostafan3ma.android.barcode11.databinding.ListItemAnalyticsSalesBinding
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Transaction

class AnalyticsAdapter_S (private val context: Context) :ListAdapter<Domain_Transaction,SalesViewHolder>(AnalyticsDiffCallBack_S()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        return SalesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,context)
    }
}

class AnalyticsDiffCallBack_S :DiffUtil.ItemCallback<Domain_Transaction>(){
    override fun areItemsTheSame(
        oldItem: Domain_Transaction,
        newItem: Domain_Transaction
    ): Boolean {
        return oldItem.transaction_id == newItem.transaction_id
    }

    override fun areContentsTheSame(
        oldItem: Domain_Transaction,
        newItem: Domain_Transaction
    ): Boolean {
        return oldItem == newItem
    }

}

class SalesViewHolder (private val binding : ListItemAnalyticsSalesBinding)
    : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:Domain_Transaction,context: Context){
            binding.itemProductId.text = item.product_name
            binding.itemProductSoldQuantity.text ="${item.transaction_quentity} PC Sold"
            binding.itemAmount.text = "${item.transaction_amount} $"
            if (item.transaction_amount > 1000.0){
                binding.itemLayout.setBackgroundColor(context.getColor(R.color.gold))
            }
        }

    companion object{
        fun from(parent: ViewGroup):SalesViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemAnalyticsSalesBinding.inflate(layoutInflater,parent,false)
            return SalesViewHolder(binding)
        }
    }

}
