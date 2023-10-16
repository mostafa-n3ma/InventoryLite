package com.mostafan3ma.android.barcode11.presentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Transaction
import com.mostafan3ma.android.barcode11.oporations.data_Mangment.repository.ShopRepository
import com.mostafan3ma.android.barcode11.oporations.utils.DataState
import com.mostafan3ma.android.barcode11.presentation.adapters.ReceiptItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Comparator

@HiltViewModel
class AnalyticsViewModel
@Inject
constructor(private val repository: ShopRepository) : ViewModel() {

    companion object{
        const val TAG = "AnalyticsViewModel"
    }

    private val _backBtnClicked = MutableLiveData<Boolean>()
    val backBtnClicked: LiveData<Boolean> get() = _backBtnClicked

    private val _quantitiesCardClicked = MutableLiveData<Boolean>()
    val quantitiesCardClicked: LiveData<Boolean> get() = _quantitiesCardClicked

    private val _salesCardClicked = MutableLiveData<Boolean>()
    val salesCardClicked: LiveData<Boolean> get() = _salesCardClicked

    private val _expiryCardClicked = MutableLiveData<Boolean>()
    val expiryCardClicked: LiveData<Boolean> get() = _expiryCardClicked

    private val _sellReceiptsCardClicked = MutableLiveData<Boolean>()
    val sellReceiptsCardClicked: LiveData<Boolean> get() = _sellReceiptsCardClicked

    private val _purchasedReceiptsCardClicked = MutableLiveData<Boolean>()
    val purchasedReceiptsCardClicked: LiveData<Boolean> get() = _purchasedReceiptsCardClicked



    lateinit var inventories:List<Domain_Inventory>
    lateinit var transactions:List<Domain_Transaction>


    private val _quantitiesList = MutableLiveData<List<Domain_Inventory>>()
    val quantitiesList:LiveData<List<Domain_Inventory>>get() = _quantitiesList


    private val _salesList = MutableLiveData<List<Domain_Transaction>>()
    val salesList:LiveData<List<Domain_Transaction>> get() = _salesList

    private val _expiryList= MutableLiveData<List<Domain_Inventory>> ()
    val expiryList:LiveData<List<Domain_Inventory>>get()=_expiryList

    private val _sellReceiptsList = MutableLiveData<List<ReceiptItem>> ()
    val sellReceiptsList:LiveData<List<ReceiptItem>> get() = _sellReceiptsList

    private val _purchaseReceiptsList = MutableLiveData<List<ReceiptItem>>()
    val purchaseReceiptsList:LiveData<List<ReceiptItem>> get() = _purchaseReceiptsList
    

    init {
        _backBtnClicked.value = false
        _quantitiesCardClicked.value = false
        _salesCardClicked.value = false
        _expiryCardClicked.value = false
        _sellReceiptsCardClicked.value = false
        _purchasedReceiptsCardClicked.value = false



        _quantitiesList.value = listOf()
        _salesList.value = listOf()
        _expiryList.value = listOf()



        fetchData()




    }



    private fun fetchData() {
        viewModelScope.launch {
            repository.get_Inventories().onEach { dataState ->
                when(dataState){
                    is DataState.Error -> Log.d(
                        TAG,
                        "fetchData: inventories Error :${dataState.exception.message}"
                    )
                    DataState.Loading -> Log.d(TAG, "fetchData: inventories Loading")
                    is DataState.Success -> {
                        inventories = dataState.data
                        Log.d(TAG, "fetchData: inventories success : data = ${dataState.data}")
                        getQuantitiesList(inventories)
                        getExpiryList(inventories)
                    }
                }
            }.launchIn(viewModelScope)


            repository.get_Transactions().onEach {dataState ->
                when(dataState){
                    is DataState.Error -> Log.d(
                        TAG,
                        "transaction: error ${dataState.exception.message}"
                    )
                    DataState.Loading -> Log.d(TAG, "transaction:Loading: ")
                    is DataState.Success -> {
                        transactions = dataState.data
                        Log.d(TAG, "fetchData: transactions : $transactions")
                        getSalesList(transactions)
                        _sellReceiptsList.value = getReceiptsLists(transactions,TRANSACTION_TYPE.SELL)
                        _purchaseReceiptsList.value = getReceiptsLists(transactions,TRANSACTION_TYPE.PURCHASE)
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    private fun getReceiptsLists(transactions: List<Domain_Transaction>,transactionType:TRANSACTION_TYPE):MutableList<ReceiptItem> {

        // sell receipts
        //filter the list and get only the sales or purchases transactions
        val filteredTransactions: List<Domain_Transaction> = when(transactionType){
            TRANSACTION_TYPE.SELL -> {
                transactions.filter { it.transaction_type == "SELL"}
            }
            TRANSACTION_TYPE.PURCHASE -> {
                transactions.filter { it.transaction_type == "PURCHASE"}
            }
        }

        // get the distinct receipts id’s in a list<String>
        val distinctReceiptsIds: List<String> = filteredTransactions.map{it.receipt_session}.distinct()
        // map through the string list of id’s and create ReceiptItem object and fill it’s properties from the sellReceipts list
        //and add it to a final receiptsItems
        val finalReceiptsItems = mutableListOf<ReceiptItem>()
        distinctReceiptsIds.map {distinctId :String->
            val tempReceipt = ReceiptItem()
            tempReceipt.receipt_id = distinctId
            filteredTransactions.map { transaction->
                if (transaction.receipt_session == tempReceipt.receipt_id){
                    tempReceipt.items_List.add(transaction)
                }
            }
            finalReceiptsItems.add(tempReceipt)
        }

        return finalReceiptsItems

    }





    private fun getExpiryList(inventories: List<Domain_Inventory>) {
        val customComparator = Comparator { domain1: Domain_Inventory, domain2: Domain_Inventory ->
            val date1 = domain1.expiration_date
            val date2 = domain2.expiration_date

            date1.compareTo(date2)
        }

        val sortedList: List<Domain_Inventory> = inventories.sortedWith(customComparator)
        _expiryList.value = sortedList

    }



    private fun getSalesList(transactions: List<Domain_Transaction>) {
        val filteredList = mutableListOf<Domain_Transaction>()
        transactions.map { transaction->
            if (transaction.transaction_type == "SELL"){
                if (filteredList.any{it.product_id == transaction.product_id}){
                    // the product exist add quantity and amount
                    val currentItem = filteredList.find { it.product_id == transaction.product_id }!!
                    currentItem.transaction_quentity +=transaction.transaction_quentity
                    currentItem.transaction_amount +=transaction.transaction_amount
                }else{
                    // the product is not existed in the list add new one
                    filteredList.add(transaction.copy())
                }
            }
        }
        _salesList.value = filteredList.sortedByDescending { it.transaction_amount }
        Log.d(TAG, "getSalesList: $filteredList")
    }




    private fun getQuantitiesList(inventories: List<Domain_Inventory>) {
        val sortedList = inventories.sortedBy { it.total_items_quantity }
        Log.d(TAG, "getQuantitiesList: sorted list : $sortedList")
        _quantitiesList.value = sortedList
    }


    fun setEvent(event: AnalyticsEvent) {
        when (event) {
            AnalyticsEvent.ClickBackBtnEvent -> {
                _backBtnClicked.value = true
                _backBtnClicked.value = false
            }
            AnalyticsEvent.ClickExpiryCardEvent -> {
                _expiryCardClicked.value =true
                _expiryCardClicked.value =false
            }
            AnalyticsEvent.ClickPurchaseReceiptsCardEvent -> {
                _purchasedReceiptsCardClicked.value = true
                _purchasedReceiptsCardClicked.value = false
            }
            AnalyticsEvent.ClickQuantitiesCardEvent -> {
                _quantitiesCardClicked.value = true
                _quantitiesCardClicked.value = false
            }
            AnalyticsEvent.ClickSalesCardEvent -> {
                _salesCardClicked.value = true
                _salesCardClicked.value = false
            }
            AnalyticsEvent.ClickSellReceiptsCardEvent -> {
                _sellReceiptsCardClicked.value = true
                _sellReceiptsCardClicked.value = false
            }
        }
    }








}


sealed class AnalyticsEvent{
    object ClickBackBtnEvent : AnalyticsEvent()
    object ClickQuantitiesCardEvent : AnalyticsEvent()
    object ClickSalesCardEvent : AnalyticsEvent()
    object ClickExpiryCardEvent : AnalyticsEvent()
    object ClickSellReceiptsCardEvent : AnalyticsEvent()
    object ClickPurchaseReceiptsCardEvent : AnalyticsEvent()
}

enum class TRANSACTION_TYPE{SELL,PURCHASE}


