package com.mostafan3ma.android.barcode11.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostafan3ma.android.barcode11.R
import com.mostafan3ma.android.barcode11.databinding.ListItemAnalyticsExpiryBinding
import com.mostafan3ma.android.barcode11.operations.data_Entities.Domain_Inventory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AnalyticsAdapter_E (private val context: Context) :ListAdapter<Domain_Inventory,ExpiresViewHolder> (AnalyticsDiffCallBack_Q()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpiresViewHolder {
        return ExpiresViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ExpiresViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,context)
    }
}

class ExpiresViewHolder (private val binding : ListItemAnalyticsExpiryBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Domain_Inventory, context: Context){
        binding.product = item
        if(isNearToBeExpired(item.expiration_date)){
            binding.itemLayout.setBackgroundColor(context.getColor(R.color.red_card))
        }
    }


    fun isNearToBeExpired(date: String): Boolean {
        // add this to the viewHolder that deals with expiry data
        return try {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = Date()
            val dateToCheck = dateFormatter.parse(date)

            val diffInMilliseconds = dateToCheck.time - currentDate.time
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds)

            diffInDays <= 10
        } catch (e: Exception) {
            // Handle parsing exceptions here
            false
        }
    }



    companion object {
        fun from(parent: ViewGroup):ExpiresViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemAnalyticsExpiryBinding.inflate(layoutInflater,parent,false)
            return ExpiresViewHolder(binding)
        }
    }

}
