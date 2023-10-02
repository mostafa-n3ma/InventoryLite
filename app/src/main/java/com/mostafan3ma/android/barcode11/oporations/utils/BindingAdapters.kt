package com.mostafan3ma.android.barcode11.oporations.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("set_text")
fun setText(textView: TextView,value:Any){
    when(value){
        is Double->{
            textView.text = "$value $"
        }
        is Int->{
            textView.text = value.toString()
        }
        else->{
            textView.text = "???"
        }
    }
}