package com.mostafan3ma.android.barcode11.presentation.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Transaction
import com.mostafan3ma.android.barcode11.oporations.data_Mangment.repository.ShopRepository
import com.mostafan3ma.android.barcode11.oporations.utils.DataState
import com.mostafan3ma.android.barcode11.oporations.utils.generateUniqueId
import com.mostafan3ma.android.barcode11.oporations.utils.getCurrentDate
import com.mostafan3ma.android.barcode11.presentation.adapters.Operation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel
@Inject
constructor(private val repository: ShopRepository) : ViewModel() {
    companion object {
        const val TAG = "PurchaseViewModel"
    }

    val receipt_total = MutableLiveData<Double>()
    fun updateReceiptTotal() {
        var totalAmount = 0.0
        localProductsList.value!!.map { iniventory ->
            totalAmount += iniventory.available_quantity_amount
        }
        receipt_total.value = totalAmount
    }


    // bottomSheet views values
    val bottom_P_id = MutableLiveData<String>()
    val bottom_P_name = MutableLiveData<String>()
    val bottom_P_img = MutableLiveData<String>()
    val bottom_item_purchase_price = MutableLiveData<String>()
    val bottom_item_selling_price = MutableLiveData<String>()
    val bottom_P_barcode = MutableLiveData<String>()
    val bottom_P_category = MutableLiveData<String>()
    val bottom_P_description = MutableLiveData<String>()
    val bottom_purchasing_amount = MutableLiveData<String>()

    val bottom_P_total_items_quantity = MutableLiveData<String>()
    val bottom_P_purchasing_date = MutableLiveData<String>()

    val bottom_P_expiration_date_Y = MutableLiveData<String>()
    val bottom_P_expiration_date_M = MutableLiveData<String>()
    val bottom_P_expiration_date_D = MutableLiveData<String>()

    val bottom_P_production_date_Y = MutableLiveData<String>()
    val bottom_P_production_date_M = MutableLiveData<String>()
    val bottom_P_production_date_D = MutableLiveData<String>()


    private val _beep = MutableLiveData<Boolean>()
    val beep: LiveData<Boolean> get() = _beep


    private val _clickDoneBtn = MutableLiveData<Boolean>()
    val clickDoneBtn: LiveData<Boolean> get() = _clickDoneBtn
    fun clickDoneBtn() {
        setEvent(PurchaseViewModelEvent.ClickDoneBtn)
    }


    private val _backBtnClicked = MutableLiveData<Boolean>()
    val backBtnClicked: LiveData<Boolean> get() = _backBtnClicked
    fun goBack() {
        setEvent(PurchaseViewModelEvent.ClickBackBtn)
    }


    private val _notifyPosition = MutableLiveData<Int?>()
    val notifyPosition: LiveData<Int?> get() = _notifyPosition

    var localProductsList = MutableLiveData<MutableList<Domain_Inventory>>()
    private val _bottomSheetStatus = MutableLiveData<Boolean>()
    val bottomSheetStatus: LiveData<Boolean> get() = _bottomSheetStatus
    fun directCloseBottomSheet() {
        setEvent(PurchaseViewModelEvent.CloseBottomSheet)
    }


    val toast_msg = MutableLiveData<String>()

    private lateinit var availableProductsList: List<Domain_Inventory>

    val namesSuggestions = MutableLiveData<MutableList<String>?>()


    private val _openBarcodeDetection = MutableLiveData<Boolean>()
    val openBarcodeDetection: LiveData<Boolean> get() = _openBarcodeDetection


    private val _barcodeBtnClicked = MutableLiveData<Boolean>()
    val barcodeBtnClicked: LiveData<Boolean> get() = _barcodeBtnClicked
    fun clickBarcodeBtn() {
        setEvent(PurchaseViewModelEvent.ClickBarcodeBtn)
    }

    private val _hideKeyBoardRequired = MutableLiveData<Boolean>()
    val hideKeyBoardRequired: LiveData<Boolean> get() = _hideKeyBoardRequired

    init {


        viewModelScope.launch {
            repository.get_Inventories().onEach { data_state ->
                when (data_state) {
                    is DataState.Error -> {
                        setEvent(PurchaseViewModelEvent.Toast_Announcement("Oops... someThing went Wrong!!"))
                        Log.d(TAG, "get_inventories Error-----> : ${data_state.exception.message}")
                    }
                    is DataState.Loading -> {
                        setEvent(PurchaseViewModelEvent.Toast_Announcement("Loading....."))
                        Log.d(TAG, "get_inventories: just loading........>")
                    }
                    is DataState.Success -> {
                        availableProductsList = data_state.data
                        namesSuggestions.value =
                            availableProductsList.map { it.product_name } as MutableList<String>
                        Log.d(TAG, "get_inventories:Success-----> data:${data_state.data}")
                    }
                }
            }.launchIn(viewModelScope)
        }

        toast_msg.value = ""
        _barcodeBtnClicked.value = false
        _openBarcodeDetection.value = true
        _bottomSheetStatus.value = false
        namesSuggestions.value = null
        _hideKeyBoardRequired.value = false
        localProductsList.value = mutableListOf()
        _backBtnClicked.value = false
        _clickDoneBtn.value = false
        receipt_total.value = 0.0
        _notifyPosition.value = null



        bottom_P_id.value = ""
        bottom_P_name.value = ""
        bottom_P_img.value = ""
        bottom_item_purchase_price.value = ""
        bottom_item_selling_price.value = ""
        bottom_P_barcode.value = ""
        bottom_P_category.value = ""
        bottom_P_description.value = ""
        bottom_purchasing_amount.value = ""
        bottom_P_total_items_quantity.value = ""
        bottom_P_purchasing_date.value = ""
        bottom_P_expiration_date_Y.value = ""
        bottom_P_expiration_date_M.value = ""
        bottom_P_expiration_date_D.value = ""
        bottom_P_production_date_Y.value = ""
        bottom_P_production_date_M.value = ""
        bottom_P_production_date_D.value = ""

    }

    private fun emptyBottomFields() {
        bottom_P_id.value = ""
        bottom_P_name.value = ""
        bottom_P_img.value = ""
        bottom_item_purchase_price.value = ""
        bottom_item_selling_price.value = ""
        bottom_P_barcode.value = ""
        bottom_P_category.value = ""
        bottom_P_description.value = ""
        bottom_purchasing_amount.value = ""
        bottom_P_total_items_quantity.value = ""
        bottom_P_purchasing_date.value = ""
        bottom_P_expiration_date_Y.value = ""
        bottom_P_expiration_date_M.value = ""
        bottom_P_expiration_date_D.value = ""
        bottom_P_production_date_Y.value = ""
        bottom_P_production_date_M.value = ""
        bottom_P_production_date_D.value = ""
    }

    fun loadToBottomFields(product: Domain_Inventory) {
        bottom_P_id.value = product.product_id.toString()
        bottom_P_name.value = product.product_name
        bottom_P_img.value = product.product_image
        bottom_item_purchase_price.value = product.item_purchase_price.toString()
        bottom_item_selling_price.value = product.item_selling_price.toString()
        bottom_P_barcode.value = product.barcode
        bottom_P_category.value = product.category
        bottom_P_description.value = product.description
        bottom_P_total_items_quantity.value = "1"
        bottom_purchasing_amount.value = product.item_purchase_price.toString()
        bottom_P_purchasing_date.value = product.purchasing_date

        val expirationDateComponents = product.expiration_date.split('-');

        // Check if there are three components (year, month, and day)
        if (expirationDateComponents.size == 3) {
            bottom_P_expiration_date_Y.value = expirationDateComponents[0];
            bottom_P_expiration_date_M.value = expirationDateComponents[1];
            bottom_P_expiration_date_D.value = expirationDateComponents[2];
        }


        val productionDateComponents = product.production_date.split('-');

        // Check if there are three components (year, month, and day)
        if (productionDateComponents.size == 3) {
            bottom_P_production_date_Y.value = productionDateComponents[0];
            bottom_P_production_date_M.value = productionDateComponents[1];
            bottom_P_production_date_D.value = productionDateComponents[2];
        }

    }


    fun setEvent(event: PurchaseViewModelEvent) {
        when (event) {
            PurchaseViewModelEvent.ClickBarcodeBtn -> {
                _barcodeBtnClicked.value = true
                _barcodeBtnClicked.value = false
            }
            is PurchaseViewModelEvent.SetBarcodeDetectorStatus -> {
                when (event.status) {
                    Detector_status.Receive -> {
                        _openBarcodeDetection.value = true
                        _openBarcodeDetection.postValue(true)
                    }
                    Detector_status.Pause -> {
                        _openBarcodeDetection.value = false
                        _openBarcodeDetection.postValue(false)
                    }
                }
            }
            is PurchaseViewModelEvent.Toast_Announcement -> {
                toast_msg.value = event.msg
                toast_msg.value = ""
            }
            is PurchaseViewModelEvent.OpenBottomSheetEvent -> {
                if (event.product == Domain_Inventory()) {
                    Log.d(TAG, "setEvent: openBottomSheet with empty object")
                    _bottomSheetStatus.value = true
                } else {
                    Log.d(TAG, "setEvent: openBottomSheet object not empty")
                    loadToBottomFields(event.product)
                    _bottomSheetStatus.value = true
                }
            }
            PurchaseViewModelEvent.CloseBottomSheet -> {
                // empty all the fields on bottom sheet
                viewModelScope.launch {
                    setEvent(PurchaseViewModelEvent.HideKeyBoardRequired)
                    delay(100)
                    emptyBottomFields()
                    _bottomSheetStatus.value = false
                }
//                setEvent(PurchaseViewModelEvent.SetBarcodeDetectorStatus(Detector_status.Receive))
            }
            PurchaseViewModelEvent.ClickBottomSheetAddBtn -> {
//                * Check if some fields are empty or unveiled
//                * create Product object by the fields data
                val addedProduct: Domain_Inventory = calculateProductInfoFromBottomFields()
//                * Add the product to the localproductslist
//                localProductsList.value!!.add(addedProduct)
                val tempList = localProductsList.value!!.toMutableList()
                tempList.add(addedProduct)
                localProductsList.value = tempList

                Log.d(
                    TAG,
                    "setEvent: clickAddBtn : localProductsList = ${localProductsList.value} "
                )
                setEvent(PurchaseViewModelEvent.UpdateReceiptTotal)
                setEvent(PurchaseViewModelEvent.CloseBottomSheet)
            }
            PurchaseViewModelEvent.HideKeyBoardRequired -> {
                _hideKeyBoardRequired.value = true
                _hideKeyBoardRequired.value = false
            }
            PurchaseViewModelEvent.ClickBackBtn -> {
                _backBtnClicked.value = true
                _backBtnClicked.value = false
            }
            PurchaseViewModelEvent.ClickDoneBtn -> {
                _clickDoneBtn.value = true
                _clickDoneBtn.value = false
            }
            PurchaseViewModelEvent.PlayBeep -> {
                _beep.value = true
                _beep.value = false
            }
            PurchaseViewModelEvent.UpdateReceiptTotal -> {
                updateReceiptTotal()
            }
            is PurchaseViewModelEvent.UpdateItem -> {
                val requiredProduct =
                    localProductsList.value!!.find { it.product_id == event.id }!!.copy()
                val index = localProductsList.value!!.indexOf(requiredProduct)
                when (event.operation) {
                    Operation.PULSE -> {
                        requiredProduct.total_items_quantity++
                        requiredProduct.available_quantity_amount =
                            requiredProduct.total_items_quantity * requiredProduct.item_selling_price
                        localProductsList.value!![index] = requiredProduct
                    }
                    Operation.MINUS -> {
                        requiredProduct.total_items_quantity--
                        requiredProduct.available_quantity_amount =
                            requiredProduct.total_items_quantity * requiredProduct.item_selling_price
                        localProductsList.value!![index] = requiredProduct
                    }
                }
                setEvent(PurchaseViewModelEvent.NotifyAdapter(event.position))
                setEvent(PurchaseViewModelEvent.UpdateReceiptTotal)

            }
            is PurchaseViewModelEvent.NotifyAdapter -> {
                _notifyPosition.value = event.position
                _notifyPosition.value = null
            }
        }
    }


    fun clickBottomAddBtn() {
        setEvent(PurchaseViewModelEvent.ClickBottomSheetAddBtn)
    }

    private fun calculateProductInfoFromBottomFields(): Domain_Inventory {
        val valide_id: String? = bottom_P_id.value
        when (valide_id) {
            "" -> {
                // new product with no id
                Log.d(TAG, "calculateProductInfoFromBottomFields: new product with no id")
                return Domain_Inventory(
                    product_name = bottom_P_name.value!!,
                    product_image = bottom_P_img.value!!,
                    item_purchase_price = bottom_item_purchase_price.value!!.toDouble(),
                    item_selling_price = bottom_item_selling_price.value!!.toDouble(),
                    barcode = bottom_P_barcode.value!!,
                    category = bottom_P_category.value!!,
                    description = bottom_P_description.value!!,
                    available_quantity_amount = bottom_purchasing_amount.value!!.toDouble(),
                    total_items_quantity = bottom_P_total_items_quantity.value!!.toInt(),
                    purchasing_date = bottom_P_purchasing_date.value!!,
                    expiration_date = bottom_P_expiration_date_Y.value + "-" + bottom_P_expiration_date_M.value + "-" + bottom_P_expiration_date_D.value,
                    production_date = bottom_P_production_date_Y.value + "-" + bottom_P_production_date_M.value + "-" + bottom_P_production_date_D.value
                )
            }
            else -> {
                // updating available product
                Log.d(TAG, "calculateProductInfoFromBottomFields: updating available product")
                return Domain_Inventory(
                    product_id = bottom_P_id.value!!.toInt(),
                    product_name = bottom_P_name.value!!,
                    product_image = bottom_P_img.value!!,
                    item_purchase_price = bottom_item_purchase_price.value!!.toDouble(),
                    item_selling_price = bottom_item_selling_price.value!!.toDouble(),
                    barcode = bottom_P_barcode.value!!,
                    category = bottom_P_category.value!!,
                    description = bottom_P_description.value!!,
                    available_quantity_amount = bottom_purchasing_amount.value!!.toDouble(),
                    total_items_quantity = bottom_P_total_items_quantity.value!!.toInt(),
                    purchasing_date = bottom_P_purchasing_date.value!!,
                    expiration_date = bottom_P_expiration_date_Y.value + "-" + bottom_P_expiration_date_M.value + "-" + bottom_P_expiration_date_D.value,
                    production_date = bottom_P_production_date_Y.value + "-" + bottom_P_production_date_M.value + "-" + bottom_P_production_date_D.value
                )
            }
        }
    }


    fun getProductObject(type: Search_type, value: String): Domain_Inventory? {
        when (type) {
            Search_type.Barcode -> {
                return availableProductsList.find { it.barcode == value }
            }
            Search_type.Text -> {
                return availableProductsList.find { it.product_name == value }
            }
            else -> {
                return null // Return null if no matching product is found
            }
        }
    }

    fun isBarcodeAvailable(code: String): Boolean {
        return availableProductsList.any { it.barcode == code }
    }


    fun done() {
        val currentDate: String = getCurrentDate()
        val receipt_id: String = generateUniqueId()

        if (localProductsList.value.isNullOrEmpty()) {
            setEvent(PurchaseViewModelEvent.Toast_Announcement("the list is empty"))
            return
        }

        viewModelScope.launch {
            val deferredResults = localProductsList.value!!.map { inventory ->
                async {
                    val savedInventory = saveInventory(inventory.copy(), currentDate)
                    recordTransaction(savedInventory, currentDate, receipt_id)
                }
            }

            // Wait for all deferred results to complete
            deferredResults.awaitAll()

            setEvent(PurchaseViewModelEvent.ClickBackBtn)
        }
    }

    private suspend fun saveInventory(
        inventory: Domain_Inventory,
        currentDate: String
    ): Domain_Inventory {
        if (availableProductsList.any() { it.product_id == inventory.product_id }) {
            //the product is available in the database ----> Update
            Log.d(TAG, "saveInventory: the product is available in the database ----> Update")
            val oldVersion: Domain_Inventory? =
                availableProductsList.find { it.product_id == inventory.product_id }
            val updatedInventory = inventory.copy()
            updatedInventory.total_items_quantity += oldVersion!!.total_items_quantity
            updatedInventory.available_quantity_amount += oldVersion.available_quantity_amount
            updatedInventory.purchasing_date = currentDate
            repository.update_Inventory(updatedInventory)
            Log.d(TAG, "saveInventory: updating inventory : ${inventory.product_name}")
            return inventory
        } else {
            // the product is not in the database inserting new product
            Log.d(TAG, "saveInventory: the product is not in the database inserting new product")
            inventory.product_id = repository.insert_Inventory(inventory).toInt()
            Log.d(
                TAG,
                "saveInventory: product inventory inserted with id = ${inventory.product_id}"
            )
//             returning the product after inserting it to the database and with the new generated id from room database
            return inventory
        }
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
            transaction_type = "PURCHASE",
            transaction_date = currentDate,
            receipt_session = receipt_id
        )
        Log.d(
            TAG,
            "recordTransaction: recording ${transaction.product_name} / receipt : ${transaction.receipt_session}"
        )
        repository.insert_Transaction(transaction)
    }

    sealed class PurchaseViewModelEvent {
        object ClickBarcodeBtn : PurchaseViewModelEvent()
        data class SetBarcodeDetectorStatus(val status: Detector_status = Detector_status.Receive) :
            PurchaseViewModelEvent()

        data class Toast_Announcement(val msg: String = "") : PurchaseViewModelEvent()
        data class OpenBottomSheetEvent(val product: Domain_Inventory = Domain_Inventory()) :
            PurchaseViewModelEvent()

        object CloseBottomSheet : PurchaseViewModelEvent()
        object ClickBottomSheetAddBtn : PurchaseViewModelEvent()
        object HideKeyBoardRequired : PurchaseViewModelEvent()
        object ClickBackBtn : PurchaseViewModelEvent()
        object ClickDoneBtn : PurchaseViewModelEvent()
        object PlayBeep : PurchaseViewModelEvent()
        object UpdateReceiptTotal : PurchaseViewModelEvent()
        data class UpdateItem(val id: Int, val position: Int, val operation: Operation) :
            PurchaseViewModelEvent()

        data class NotifyAdapter(val position: Int) : PurchaseViewModelEvent()

    }
}


enum class Search_type { Barcode, Text }
enum class Detector_status { Receive, Pause }