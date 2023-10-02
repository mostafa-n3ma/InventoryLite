package com.mostafan3ma.android.barcode11.presentation.viewModels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mostafan3ma.android.barcode11.oporations.data_Mangment.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel
@Inject
constructor(private val repository: ShopRepository) : ViewModel() {

    private val _clickInventoryCard = MutableLiveData<Boolean>()
    val clickInventoryCard:LiveData<Boolean>get()=_clickInventoryCard

    fun clickInventoryCard(){
        setDashBoardEvent(DashboardViewModelEvents.ClickInventoryCard)
    }


    private val _clickAddProductsCard = MutableLiveData<Boolean>()
    val clickAddProductsCard:LiveData<Boolean>get()=_clickAddProductsCard

    fun clickAddProductsCard(){
       setDashBoardEvent(DashboardViewModelEvents.ClickAddProductsCard)
    }

    private val _clickSellReceiptCard = MutableLiveData<Boolean>()
    val clickSellReceiptCard:LiveData<Boolean>get()=_clickSellReceiptCard
    fun clickSellReceiptCard(){
        setDashBoardEvent(DashboardViewModelEvents.ClickSellReceiptCard)
    }

    private val _clickAnalyticsCard = MutableLiveData<Boolean>()
    val clickAnalyticsCard :LiveData<Boolean>get()=_clickAnalyticsCard
    fun clickAnalyticsCard(){
      setDashBoardEvent(DashboardViewModelEvents.ClickAnalyticsCard)
    }

    private val _clickTodaySalesCard = MutableLiveData<Boolean>()
    val clickTodaySalesCard:LiveData<Boolean>get()=_clickTodaySalesCard
    fun clickTodaySalesCard(){
        setDashBoardEvent(DashboardViewModelEvents.ClickTodaySalesCArd)
    }

    private val _clickRunOffProductsCard = MutableLiveData<Boolean>()
    val clickRunOffProductsCard:LiveData<Boolean>get()=_clickRunOffProductsCard
    fun clickRunOffProductsCard(){
       setDashBoardEvent(DashboardViewModelEvents.ClickRunOffProductsCard)
    }



    init {
        _clickInventoryCard.value=false
        _clickSellReceiptCard.value=false
        _clickAnalyticsCard.value=false
        _clickAddProductsCard.value=false
        _clickTodaySalesCard.value=false
        _clickRunOffProductsCard.value=false
    }



    fun setDashBoardEvent(event: DashboardViewModelEvents){
        when(event){
            DashboardViewModelEvents.ClickInventoryCard->{
                _clickInventoryCard.value=true
                _clickInventoryCard.value=false
            }
            DashboardViewModelEvents.ClickAddProductsCard -> {
                _clickAddProductsCard.value=true
                _clickAddProductsCard.value=false
            }
            DashboardViewModelEvents.ClickAnalyticsCard -> {
                _clickAnalyticsCard.value=true
                _clickAnalyticsCard.value=false
            }
            DashboardViewModelEvents.ClickRunOffProductsCard -> {
                _clickRunOffProductsCard.value=true
                _clickRunOffProductsCard.value=false
            }
            DashboardViewModelEvents.ClickSellReceiptCard -> {
                _clickSellReceiptCard.value=true
                _clickSellReceiptCard.value=false
            }
            DashboardViewModelEvents.ClickTodaySalesCArd -> {
                _clickTodaySalesCard.value=true
                _clickTodaySalesCard.value=false
            }
        }
    }


}

sealed class DashboardViewModelEvents(){
    object ClickInventoryCard:DashboardViewModelEvents()
    object ClickSellReceiptCard:DashboardViewModelEvents()
    object ClickAnalyticsCard:DashboardViewModelEvents()
    object ClickAddProductsCard:DashboardViewModelEvents()
    object ClickTodaySalesCArd:DashboardViewModelEvents()
    object ClickRunOffProductsCard:DashboardViewModelEvents()
}