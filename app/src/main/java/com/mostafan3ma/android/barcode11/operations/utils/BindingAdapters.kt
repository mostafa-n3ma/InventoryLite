package com.mostafan3ma.android.barcode11.operations.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mostafan3ma.android.barcode11.R

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



@BindingAdapter("getInternalImg")
fun getInternalImg(imageView: ImageView, imgName:String?){
    val superImageController=SuperImageController()
    if (imgName!=null) {
        val tempBitMap = superImageController.getImageFromInternalStorage(
            imageView.context,
            imgName,
            R.drawable.add_image
        )
        imageView.setImageBitmap(tempBitMap)
    }
}