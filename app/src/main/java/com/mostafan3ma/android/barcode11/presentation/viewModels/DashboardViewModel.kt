package com.mostafan3ma.android.barcode11.presentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostafan3ma.android.barcode11.operations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.operations.data_Entities.Domain_Transaction
import com.mostafan3ma.android.barcode11.operations.data_Management.repository.ShopRepository
import com.mostafan3ma.android.barcode11.operations.utils.DataState
import com.mostafan3ma.android.barcode11.operations.utils.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel
@Inject
constructor(private val repository: ShopRepository) : ViewModel() {
companion object{
    const val TAG = "DashboardViewModel"
}
    private val _clickInventoryCard = MutableLiveData<Boolean>()
    val clickInventoryCard: LiveData<Boolean> get() = _clickInventoryCard

    fun clickInventoryCard() {
        setDashBoardEvent(DashboardViewModelEvents.ClickInventoryCard)
    }


    private val _clickAddProductsCard = MutableLiveData<Boolean>()
    val clickAddProductsCard: LiveData<Boolean> get() = _clickAddProductsCard

    fun clickAddProductsCard() {
        setDashBoardEvent(DashboardViewModelEvents.ClickAddProductsCard)
    }

    private val _clickSellReceiptCard = MutableLiveData<Boolean>()
    val clickSellReceiptCard: LiveData<Boolean> get() = _clickSellReceiptCard
    fun clickSellReceiptCard() {
        setDashBoardEvent(DashboardViewModelEvents.ClickSellReceiptCard)
    }

    private val _clickAnalyticsCard = MutableLiveData<Boolean>()
    val clickAnalyticsCard: LiveData<Boolean> get() = _clickAnalyticsCard
    fun clickAnalyticsCard() {
        setDashBoardEvent(DashboardViewModelEvents.ClickAnalyticsCard)
    }

    private val _clickTodaySalesCard = MutableLiveData<Boolean>()
    val clickTodaySalesCard: LiveData<Boolean> get() = _clickTodaySalesCard
    fun clickTodaySalesCard() {
        setDashBoardEvent(DashboardViewModelEvents.ClickTodaySalesCArd)
    }

    private val _clickRunOffProductsCard = MutableLiveData<Boolean>()
    val clickRunOffProductsCard: LiveData<Boolean> get() = _clickRunOffProductsCard
    fun clickRunOffProductsCard() {
        setDashBoardEvent(DashboardViewModelEvents.ClickRunOffProductsCard)
    }


    val _todaySales = MutableLiveData<Double>()
    val soldOutProduct = MutableLiveData<String>()

    init {
        _clickInventoryCard.value = false
        _clickSellReceiptCard.value = false
        _clickAnalyticsCard.value = false
        _clickAddProductsCard.value = false
        _clickTodaySalesCard.value = false
        _clickRunOffProductsCard.value = false
        _todaySales.value = 0.0
        soldOutProduct.value = "all products are available"
        updateSoldOutProduct()
        updateTodaySales()

    }

    private fun updateSoldOutProduct() {
        viewModelScope.launch {
            repository.get_Inventories().onEach { dataState: DataState<List<Domain_Inventory>> ->
            when(dataState){
                is DataState.Error -> {
                    Log.d(TAG, "updateSoldOutProduct: Error : ${dataState.exception.message}")}
                DataState.Loading -> Log.d(TAG, "updateSoldOutProduct: Loading")
                is DataState.Success -> {
                    val inventories: List<Domain_Inventory> = dataState.data
                    inventories.map { inventory->
                        if (inventory.total_items_quantity <1){
                            soldOutProduct.value = inventory.product_name
                        }

                    }

                }
            }

            }.launchIn(viewModelScope)
        }
    }

    private fun updateTodaySales() {
        val todayDate: String = getCurrentDate()
        var tempTotal = 0.0
        viewModelScope.launch {
            repository.get_Transactions().onEach { dataState: DataState<List<Domain_Transaction>> ->
            when(dataState){
                is DataState.Error -> {
                    Log.d(TAG, "updateTodaySales: Error:${dataState.exception.message}")}
                DataState.Loading -> {
                    Log.d(TAG, "updateTodaySales: Loading")}
                is DataState.Success -> {
                    val transactions: List<Domain_Transaction> = dataState.data
                    transactions.map { domainTransaction: Domain_Transaction ->
                        if (domainTransaction.transaction_date == todayDate && domainTransaction.transaction_type == "SELL"){
                            tempTotal +=domainTransaction.transaction_amount
                        }
                    }
                    _todaySales.value = tempTotal
                }
            }

            }.launchIn(viewModelScope)
        }
    }


    fun setDashBoardEvent(event: DashboardViewModelEvents) {
        when (event) {
            DashboardViewModelEvents.ClickInventoryCard -> {
                _clickInventoryCard.value = true
                _clickInventoryCard.value = false
            }
            DashboardViewModelEvents.ClickAddProductsCard -> {
                _clickAddProductsCard.value = true
                _clickAddProductsCard.value = false
            }
            DashboardViewModelEvents.ClickAnalyticsCard -> {
                _clickAnalyticsCard.value = true
                _clickAnalyticsCard.value = false
            }
            DashboardViewModelEvents.ClickRunOffProductsCard -> {
                _clickRunOffProductsCard.value = true
                _clickRunOffProductsCard.value = false
            }
            DashboardViewModelEvents.ClickSellReceiptCard -> {
                _clickSellReceiptCard.value = true
                _clickSellReceiptCard.value = false
            }
            DashboardViewModelEvents.ClickTodaySalesCArd -> {
                _clickTodaySalesCard.value = true
                _clickTodaySalesCard.value = false
            }
        }
    }


}

sealed class DashboardViewModelEvents() {
    object ClickInventoryCard : DashboardViewModelEvents()
    object ClickSellReceiptCard : DashboardViewModelEvents()
    object ClickAnalyticsCard : DashboardViewModelEvents()
    object ClickAddProductsCard : DashboardViewModelEvents()
    object ClickTodaySalesCArd : DashboardViewModelEvents()
    object ClickRunOffProductsCard : DashboardViewModelEvents()
}