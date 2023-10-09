package com.mostafan3ma.android.barcode11.presentation.adapters

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.barcode11.databinding.ListItemInventoryBinding
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory

class InventoriesAdapter (private val listener:InventoriesListener):ListAdapter<Domain_Inventory, InventoriesAdapter.InventoriesViewHolder>(InventoriesDiffCallBack()) {
    class InventoriesViewHolder (private val binding:ListItemInventoryBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(item:Domain_Inventory,listener: InventoriesListener,position: Int){
            binding.product = item
            // do the expand animation
            binding.itemCard.setOnClickListener {
                listener.onClick(item.product_id,position)
                checkCardExpendableStatus(binding)
            }
        }

        private fun checkCardExpendableStatus(binding: ListItemInventoryBinding) {
            if (binding.whiteLayout.visibility == View.GONE){
                TransitionManager.beginDelayedTransition(binding.itemCard, AutoTransition())
                binding.whiteLayout.visibility = View.VISIBLE
                binding.blackLayout.visibility =View.GONE
            }else{
                TransitionManager.beginDelayedTransition(binding.itemCard, AutoTransition())
                binding.whiteLayout.visibility = View.GONE
                binding.blackLayout.visibility =View.VISIBLE
            }
        }

        companion object{
            fun from(parent: ViewGroup):InventoriesViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemInventoryBinding.inflate(layoutInflater)
                return InventoriesViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoriesViewHolder {
        return InventoriesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: InventoriesViewHolder, position: Int) {
        val inventory = getItem(position)
        holder.bind(inventory,listener, position)
    }
}

class InventoriesDiffCallBack:DiffUtil.ItemCallback<Domain_Inventory>() {
    override fun areItemsTheSame(oldItem: Domain_Inventory, newItem: Domain_Inventory): Boolean {
        return oldItem.product_id == newItem.product_id
    }

    override fun areContentsTheSame(oldItem: Domain_Inventory, newItem: Domain_Inventory): Boolean {
        return oldItem == newItem
    }

}

class InventoriesListener (val listener:(id:Int,position:Int)-> Unit){
    fun onClick(id:Int,position:Int)= listener(id,position)
}
