package com.mostafan3ma.android.barcode11.presentation.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.mostafan3ma.android.barcode11.operations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.operations.data_Entities.Domain_Transaction
import com.mostafan3ma.android.barcode11.operations.data_Management.repository.ShopRepository
import com.mostafan3ma.android.barcode11.operations.utils.DataState
import com.mostafan3ma.android.barcode11.operations.utils.generateUniqueId
import com.mostafan3ma.android.barcode11.operations.utils.getCurrentDate
import com.mostafan3ma.android.barcode11.presentation.adapters.Operation
import com.mostafan3ma.android.barcode11.presentation.viewModels.InputType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellViewModel
@Inject
constructor(private val repository: ShopRepository) : ViewModel() {

    companion object {
        const val TAG = "SellViewModel"
    }


    val receipt_total = MutableLiveData<Double>()
    fun updateReceiptTotal(){
        var totalAmount = 0.0
        _receiptList.value !!.map { invnetory->
            totalAmount+=invnetory.available_quantity_amount
        }
        receipt_total.value = totalAmount
    }


    private val _beep = MutableLiveData<Boolean>()
    val beep:LiveData<Boolean>get() = _beep


    private val _detectorOpened = MutableLiveData<Boolean>()
    val detectorOpened: LiveData<Boolean> get() = _detectorOpened

    private val _barcodeBtnClicked = MutableLiveData<Boolean>()
    val barcodeBtnClicked: LiveData<Boolean> get() = _barcodeBtnClicked
    fun clickBarcodeBtn() {
        setEvent(SellViewModelEvent.ClickBarcodeBtn)
    }

    private val _hideKeyBoardRequired = MutableLiveData<Boolean>()
    val hideKeyBoardRequired: LiveData<Boolean> get() = _hideKeyBoardRequired


    fun clickDoneBtn() {
        setEvent(SellViewModelEvent.ClickDoneBtn)
    }


    private val _backBtnClicked = MutableLiveData<Boolean>()
    val backBtnClicked: LiveData<Boolean> get() = _backBtnClicked
    fun goBack() {
        setEvent(SellViewModelEvent.ClickBackBtn)
    }

    var inventories = listOf<Domain_Inventory>()
    private val _receiptList = MutableLiveData<MutableList<Domain_Inventory>>()
    val receiptList: LiveData<MutableList<Domain_Inventory>> get() = _receiptList

    val namesSuggestions = MutableLiveData<MutableList<String>?>()


    private val _toastMsg = MutableLiveData<String>()
    val toastMSg: LiveData<String> get() = _toastMsg

    private val _notifyPosition = MutableLiveData<Int?>()
    val notifyPosition:LiveData<Int?>get()=_notifyPosition

    init {

        _detectorOpened.value = true
        _barcodeBtnClicked.value = false
        _hideKeyBoardRequired.value = false
        _backBtnClicked.value = false
        _receiptList.value = mutableListOf()
        inventories = listOf()
        _toastMsg.value = ""
        namesSuggestions.value = null
        _notifyPosition.value = null
        receipt_total.value = 0.0

        viewModelScope.launch {
            repository.get_Inventories().onEach { data_state ->
                when (data_state) {
                    is DataState.Error -> {
                        Log.d(
                            TAG,
                            "get_inventories Error-----> : ${data_state.exception.message}"
                        )
                    }
                    is DataState.Loading -> {
                        Log.d(TAG, "get_inventories: just loading........>")
                    }
                    is DataState.Success -> {
                        inventories = data_state.data
                        namesSuggestions.value =
                            inventories.map { it.product_name } as MutableList<String>
                        Log.d(
                            TAG,
                            "get_inventories:Success-----> data:${data_state.data}"
                        )
                        Log.d(
                            TAG,
                            "get_inventories xxx99 test1 : ${inventories.find { it.product_id == 1 }}"
                        )

                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun setEvent(event: SellViewModelEvent) {
        when (event) {
            SellViewModelEvent.ClickBackBtn -> {
                _backBtnClicked.value = true
                _backBtnClicked.value = false
            }
            SellViewModelEvent.ClickBarcodeBtn -> {
                _barcodeBtnClicked.value = true
                _barcodeBtnClicked.value = false
            }
            SellViewModelEvent.ClickDoneBtn -> {
                Log.d(
                    TAG,
                    "setEvent:xxx99 ClickDoneBtn:test.quantity:::${inventories.find { it.product_id == 1 }}"
                )
                done()
            }
            is SellViewModelEvent.SetBarcodeDetectorStatus -> {
                when (event.status) {
                    Detector_status.Receive -> {
                        _detectorOpened.value = true
                    }
                    Detector_status.Pause -> {
                        _detectorOpened.value = false
                    }
                }
            }
            is SellViewModelEvent.UpdateReceiptList -> {
                updateReceipt(event.input, event.inputType)
                updateReceiptTotal()
                setEvent(SellViewModelEvent.HideKeyBoardEvent)
            }
            SellViewModelEvent.HideKeyBoardEvent -> {
                _hideKeyBoardRequired.value = true
                _hideKeyBoardRequired.value = false
            }
            is SellViewModelEvent.AnnounceToast -> {
                _toastMsg.value = event.msg
            }
            is SellViewModelEvent.UpdateItem ->{
                val requiredProduct = _receiptList.value!!.find { it.product_id == event.id }!!.copy()
                val index = _receiptList.value!!.indexOf(requiredProduct)
                when(event.operation){
                    Operation.PULSE -> {
                        requiredProduct.total_items_quantity++
                        requiredProduct.available_quantity_amount = requiredProduct.total_items_quantity * requiredProduct.item_selling_price
                        _receiptList.value!![index] = requiredProduct
                    }
                    Operation.MINUS -> {
                        requiredProduct.total_items_quantity--
                        requiredProduct.available_quantity_amount = requiredProduct.total_items_quantity * requiredProduct.item_selling_price
                        _receiptList.value!![index] = requiredProduct
                    }
                }
                setEvent(SellViewModelEvent.NotifyAdapter(event.position))
                updateReceiptTotal()
            }
            is SellViewModelEvent.NotifyAdapter -> {
                _notifyPosition.value = event.position
                _notifyPosition.value = null
            }
            SellViewModelEvent.PlayBeep -> {
                _beep.value = true
                _beep.value = false
            }
        }
    }


    private fun getProductObject(input: String, inputType: InputType): Domain_Inventory {
        return when (inputType) {
            TEXT -> {
                Log.d(TAG, "getProductObject:test1  ${inventories.find { it.product_name == input }!!} ")
                inventories.find { it.product_name == input }!!.copy()
            }
            BARCODE -> {
                inventories.find { it.barcode == input }!!.copy()
            }
        }
    }

    fun isAvailable(input: String, inputType: InputType): Boolean {
        //checks if the product is available in the database or not
        return when (inputType) {
            TEXT -> {
                inventories.any { it.product_name == input }
            }
            BARCODE -> {
                inventories.any { it.barcode == input }
            }
        }
    }


    private fun updateReceipt(input: String, inputType: InputType) {
        Log.d(TAG, "updateReceipt: start input:$input , inputType:$inputType ")
        var currentProduct = when (inputType) {
            TEXT -> {
                Log.d(TAG, "updateReceipt: getting currentProduct: inputType:text")
                _receiptList.value!!.find { it.product_name == input }
            }
            BARCODE -> {
                Log.d(TAG, "updateReceipt: getting currentProduct: inputType:barcode")
                _receiptList.value!!.find { it.barcode == input }
            }
        }

        if (currentProduct != null) {
            // The product already added to the receiptList → update it (quantity ++)
            Log.d(
                TAG,
                "updateReceipt: currentProduct != null The product already added to the receiptList // currentProduct = $currentProduct"
            )
            currentProduct.total_items_quantity++
            currentProduct.available_quantity_amount =
                currentProduct.item_selling_price * currentProduct.total_items_quantity
            _receiptList.value!![_receiptList.value!!.indexOf(currentProduct)] = currentProduct
            Log.d(
                TAG,
                "updateReceipt: currentProduct after updating it (quantity + available amount)"
            )
        } else {
            // Check if the product is available in the database and get its object and add (quantity = 1)
            Log.d(
                TAG,
                "updateReceipt: the product is not in the receipt list check the database>>>"
            )
            when (inputType) {
                TEXT -> {
                    if (isAvailable(input, TEXT)) {
                        currentProduct = getProductObject(input, TEXT)
                        Log.d(
                            TAG,
                            "updateReceipt: getting the product from the databse currentProduct : $currentProduct"
                        )
                        currentProduct.total_items_quantity = 1
                        currentProduct.available_quantity_amount = currentProduct.item_selling_price
                        _receiptList.value!!.add(currentProduct)
                        Log.d(
                            TAG,
                            "updateReceipt: adding the currentProduct to the receipt list with quantity = 1 // currentProduct :$currentProduct "
                        )
                    } else {
                        Log.d(TAG, "updateReceipt: Product not available in the database")
                        setEvent(SellViewModelEvent.AnnounceToast("Product not available"))
                    }
                }
                BARCODE -> {
                    if (isAvailable(input, InputType.BARCODE)) {
                        currentProduct = getProductObject(input, BARCODE)
                        Log.d(
                            TAG,
                            "updateReceipt: getting the product from the databse currentProduct : $currentProduct"
                        )
                        currentProduct.total_items_quantity = 1
                        currentProduct.available_quantity_amount = currentProduct.item_selling_price
                        _receiptList.value!!.add(currentProduct)
                        Log.d(
                            TAG,
                            "updateReceipt: adding the currentProduct to the receipt list with quantity = 1 // currentProduct :$currentProduct "
                        )
                    } else {
                        Log.d(TAG, "updateReceipt: Product not available in the database")
                        setEvent(SellViewModelEvent.AnnounceToast("Product not available"))
                    }
                }
            }
        }
    }


    fun done() {
        val currentDate: String = getCurrentDate()
        val receipt_id: String = generateUniqueId()

        if (_receiptList.value!!.isEmpty()) {
            setEvent(SellViewModelEvent.AnnounceToast("the list is empty"))
            return
        }


        viewModelScope.launch {

            val deferredResults = _receiptList.value!!.map { inventory ->
                async {
                    sellInventory(inventory.copy())
                    recordTransaction(inventory.copy(), currentDate, receipt_id)
                }
            }

            // Wait for all deferred results to complete
            deferredResults.awaitAll()

            setEvent(SellViewModelEvent.ClickBackBtn)
        }
    }

    private suspend fun sellInventory(inventory: Domain_Inventory) {
        val original_inventory: Domain_Inventory? =
            inventories.find { it.product_id == inventory.product_id }
        Log.d(TAG, "sellInventory: xxx99 originalInventory:$original_inventory")
        inventory.total_items_quantity = original_inventory!!.total_items_quantity - inventory.total_items_quantity
        inventory.available_quantity_amount = original_inventory.available_quantity_amount - inventory.available_quantity_amount
        Log.d(TAG, "sellInventory: xxx99 update inventory:$inventory")
        repository.update_Inventory(inventory)
    }


    private suspend fun recordTransaction(
        inventory: Domain_Inventory,
        currentDate: String,
        receipt_id: String
    ) {
        val transaction = Domain_Transaction(
            product_id = inventory.product_id,
            product_name = inventory.product_name,
            product_image = inventory.product_image,
            item_purchase_price = inventory.item_purchase_price,
            item_selling_price = inventory.item_selling_price,
            barcode = inventory.barcode,
            category = inventory.category,
            description = inventory.description,
            expiration_date = inventory.expiration_date,
            production_date = inventory.production_date,
            transaction_quentity = inventory.total_items_quantity,
            transaction_amount = inventory.available_quantity_amount,
            transaction_type = "SELL",
            transaction_date = currentDate,
            receipt_session = receipt_id
        )
        repository.insert_Transaction(transaction)
    }


}

sealed class SellViewModelEvent() {
    data class SetBarcodeDetectorStatus(val status: Detector_status = Detector_status.Receive) :
        SellViewModelEvent()

    object ClickBarcodeBtn : SellViewModelEvent()
    object ClickDoneBtn : SellViewModelEvent()
    object ClickBackBtn : SellViewModelEvent()
    data class UpdateReceiptList(val input: String, val inputType: InputType) : SellViewModelEvent()
    object HideKeyBoardEvent : SellViewModelEvent()
    data class AnnounceToast(val msg: String = "") : SellViewModelEvent()
    data class UpdateItem(val id: Int,val position:Int, val operation:Operation):SellViewModelEvent()
    data class NotifyAdapter(val position: Int):SellViewModelEvent()
    object PlayBeep:SellViewModelEvent()

}

enum class InputType { TEXT, BARCODE }