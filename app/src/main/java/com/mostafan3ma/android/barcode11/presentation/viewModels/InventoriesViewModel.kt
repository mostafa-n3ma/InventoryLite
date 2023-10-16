package com.mostafan3ma.android.barcode11.presentation.viewModels

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.oporations.data_Mangment.repository.ShopRepository
import com.mostafan3ma.android.barcode11.oporations.utils.DataState
import com.mostafan3ma.android.barcode11.presentation.viewModels.FilterType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoriesViewModel
@Inject
constructor(private val repository: ShopRepository) : ViewModel() {
    companion object {
        const val TAG = "InventoriesViewModel"
    }


    private val _saveNewImg = MutableLiveData<String?>()
    val saveNewImg:LiveData<String?>get() = _saveNewImg

    private val _returnedBottomImgUri = MutableLiveData<Uri?>()
    val returnedBottomImgUri:LiveData<Uri?>get() = _returnedBottomImgUri


    private val _openImgChooser = MutableLiveData<Boolean>()
    val openImgChooser:LiveData<Boolean>get() = _openImgChooser
    fun openImgChooser(){
        setEvent(InventoriesEvents.OpenImgChooser)
    }



    private val _clickable=MutableLiveData<Boolean>()
    val clickable:LiveData<Boolean>get() = _clickable



    private val _editBottomSheetOpened = MutableLiveData<Boolean>()
    val editBottomSheetOpened:LiveData<Boolean> get() = _editBottomSheetOpened
    fun directCloseBottomSheet(){
        setEvent(InventoriesEvents.CloseEditBottomSheet)
    }



    val bottom_P_id = MutableLiveData<String>()
    val bottom_P_name = MutableLiveData<String>()
    val bottom_P_img = MutableLiveData<String>()
    val bottom_packaging_purchase_price = MutableLiveData<String>()
    val bottom_packaging_selling_price = MutableLiveData<String>()
    val bottom_item_purchase_price = MutableLiveData<String>()
    val bottom_item_selling_price = MutableLiveData<String>()
    val bottom_P_barcode = MutableLiveData<String>()
    val bottom_P_category = MutableLiveData<String>()
    val bottom_P_description = MutableLiveData<String>()
    val bottom_P_packaging = MutableLiveData<String>()
    val bottom_available_quantity_amount = MutableLiveData<String>()
    val bottom_P_total_package_quantity = MutableLiveData<String>()
    val bottom_P_total_items_quantity = MutableLiveData<String>()
    val bottom_P_purchasing_date = MutableLiveData<String>()

    val bottom_P_expiration_date_Y = MutableLiveData<String>()
    val bottom_P_expiration_date_M = MutableLiveData<String>()
    val bottom_P_expiration_date_D = MutableLiveData<String>()

    val bottom_P_production_date_Y = MutableLiveData<String>()
    val bottom_P_production_date_M = MutableLiveData<String>()
    val bottom_P_production_date_D = MutableLiveData<String>()




    private val _detectorOpened = MutableLiveData<Boolean>()
    val detectorOpened: LiveData<Boolean> get() = _detectorOpened

    private val _barcodeBtnClicked = MutableLiveData<Boolean>()
    val barcodeBtnClicked: LiveData<Boolean> get() = _barcodeBtnClicked
    fun clickBarcodeBtn() {
        if (_clickable.value!!){
            setEvent(InventoriesEvents.ClickBarcodeBtn)
        }
    }

    private val _hideKeyBoardRequired = MutableLiveData<Boolean>()
    val hideKeyBoardRequired: LiveData<Boolean> get() = _hideKeyBoardRequired



    private val _backBtnClicked = MutableLiveData<Boolean>()
    val backBtnClicked: LiveData<Boolean> get() = _backBtnClicked
    fun goBack() {
        if (_clickable.value ==true){
            setEvent(InventoriesEvents.ClickBackBtn)
        }
    }



    var inventories = listOf<Domain_Inventory>()
    val filteredList = MutableLiveData<MutableList<Domain_Inventory>>()
    var categoriesList = MutableLiveData<List<String>>()
    val namesSuggestions = MutableLiveData<MutableList<String>?>()



    init {
        filteredList.value = mutableListOf()
        categoriesList.value = listOf()
        namesSuggestions.value = null
        _detectorOpened.value = true
        _editBottomSheetOpened.value =false
        _clickable.value=true
        _openImgChooser.value =false
        _returnedBottomImgUri.value =null
        _saveNewImg.value =null


        bottom_P_id.value = ""
        bottom_P_name.value = ""
        bottom_P_img.value = ""
        bottom_packaging_purchase_price.value = "0.0"
        bottom_packaging_selling_price.value = "0.0"
        bottom_item_purchase_price.value = "0.0"
        bottom_item_selling_price.value = "0.0"
        bottom_P_barcode.value = ""
        bottom_P_category.value = ""
        bottom_P_description.value = ""
        bottom_P_packaging.value = "1"
        bottom_available_quantity_amount.value = "0.0"
        bottom_P_total_package_quantity.value = "0"
        bottom_P_total_items_quantity.value = "0"
        bottom_P_purchasing_date.value = ""
        bottom_P_expiration_date_Y.value = ""
        bottom_P_expiration_date_M.value = ""
        bottom_P_expiration_date_D.value = ""
        bottom_P_production_date_Y.value = ""
        bottom_P_production_date_M.value = ""
        bottom_P_production_date_D.value = ""




        updateInventories()
    }


    fun setEvent(events: InventoriesEvents) {
        when (events) {
            InventoriesEvents.ClickBackBtn -> {
                _backBtnClicked.value =true
                _backBtnClicked.value =false
            }
            InventoriesEvents.ClickBarcodeBtn -> {
                _barcodeBtnClicked.value =true
                _barcodeBtnClicked.value =false
            }
            is InventoriesEvents.FilterInventories -> {
                setFilter(events.input,events.filterType)
            }
            InventoriesEvents.HideKeyBoard -> {
                _hideKeyBoardRequired.value =true
                _hideKeyBoardRequired.value =false
            }
            is InventoriesEvents.OpenEditBottomSheet -> {
                _editBottomSheetOpened.value = true
                loadToBottomFields(getProductObject(events.product_id))
                setEvent(InventoriesEvents.ClickableEnabled(false))
            }
            is InventoriesEvents.SetBarcodeStatus -> {
                when(events.detectorStatus){
                    Detector_status.Receive -> {_detectorOpened.value = true}
                    Detector_status.Pause -> {_detectorOpened.value = false}
                }
            }
            InventoriesEvents.CloseEditBottomSheet -> {
                viewModelScope.launch{
                    setEvent(InventoriesEvents.HideKeyBoard)
                    delay(100)
                    _editBottomSheetOpened.value = false
                    setEvent(InventoriesEvents.ClickableEnabled(true))
                    _returnedBottomImgUri.value =null
                    _saveNewImg.value =null
                }

            }
            is InventoriesEvents.ClickableEnabled -> {
                when(events.status){
                    true -> {
                        _clickable.value = true
                        emptyBottomFields()
                    }
                    false -> _clickable.value=false
                }
            }
            InventoriesEvents.EditBtnClicked -> {
                if (returnedBottomImgUri.value!=null){
                    setEvent(InventoriesEvents.SaveNewImg(bottom_P_name.value!! + "_" + bottom_P_id.value))
                    bottom_P_img.value = bottom_P_name.value!! + "_" + bottom_P_id.value
                }
                editProduct(calculateProductDataFromBottomFields())
                updateInventories()
                setEvent(InventoriesEvents.CloseEditBottomSheet)
            }
            InventoriesEvents.OpenImgChooser -> {
                _openImgChooser.value =true
                _openImgChooser.value =false
            }
            is InventoriesEvents.GetReturnedImgUri -> {
                _returnedBottomImgUri.value = events.uri
            }
            is InventoriesEvents.SaveNewImg -> {
                _saveNewImg.value = events.product_img
            }
        }
    }


    private fun updateInventories() {
        viewModelScope.launch {

            repository.get_Inventories().onEach {dataState: DataState<List<Domain_Inventory>> ->
                when(dataState){
                    is DataState.Error -> {
                        Log.d(TAG, "init: error getting inventories")
                    }
                    DataState.Loading -> {
                        Log.d(TAG, "init: loading inventories")
                    }
                    is DataState.Success -> {
                        inventories = dataState.data
                        Log.d(TAG, "init: inventories: ${dataState.data}")
                        categoriesList.value = dataState.data.map { it.category }.distinct()
                        Log.d(TAG, "init: categories: $categoriesList")
                        val filtration = mutableListOf<Domain_Inventory>()
                        inventories.map {
                            filtration.add(it)
                        }
                        filteredList.value=filtration
                        filteredList.postValue(filtration)
                        Log.d(TAG, "init: filteredList:${filteredList.value}")
                        namesSuggestions.value =
                            inventories.map { it.product_name } as MutableList<String>
                    }
                }
            }.launchIn(viewModelScope)
        }
    }


    private fun  setFilter(input:String ,filterType:FilterType){
        val inventoriesCopy = inventories.toList()
        when(filterType){
            CATEGORY -> {
                filteredList.value = inventoriesCopy.filter { it.category == input }.toMutableList()
                Log.d(TAG, "setFilter: CATEGORY -> ${filteredList.value}")
            }
            NAME -> {
                filteredList.value = inventoriesCopy.filter { it.product_name == input }.toMutableList()
                Log.d(TAG, "setFilter: NAME -> ${filteredList.value}")
            }
            BARCODE -> {
                filteredList.value = inventoriesCopy.filter { it.barcode == input }.toMutableList()
                Log.d(TAG, "setFilter: BARCODE -> ${filteredList.value}")
            }
            NOTHING -> {
                filteredList.value = inventoriesCopy.sortedBy { it.category }.toMutableList()
                Log.d(TAG, "setFilter: NOTHING -> ${filteredList.value}")
            }
        }
    }



    private fun getProductObject(id: Int): Domain_Inventory {
        return inventories.find { it.product_id == id }!!.copy()
    }

    fun clickEditBtn(){
        setEvent(InventoriesEvents.EditBtnClicked)
    }
    fun editProduct(product:Domain_Inventory){
        viewModelScope.launch {
            repository.update_Inventory(product)
        }
    }


    fun loadToBottomFields(product: Domain_Inventory) {
        bottom_P_id.value = product.product_id.toString()
        bottom_P_name.value = product.product_name
        bottom_P_img.value = product.product_image
        bottom_packaging_purchase_price.value = product.packaging_purchase_price.toString()
        bottom_packaging_selling_price.value = product.packaging_selling_price.toString()
        bottom_item_purchase_price.value = product.item_purchase_price.toString()
        bottom_item_selling_price.value = product.item_selling_price.toString()
        bottom_P_barcode.value = product.barcode
        bottom_P_category.value = product.category
        bottom_P_description.value = product.description
        bottom_P_packaging.value = product.packaging.toString()
        bottom_available_quantity_amount.value = product.available_quantity_amount.toString()
        bottom_P_total_package_quantity.value = product.total_package_quantity.toString()
        bottom_P_total_items_quantity.value = product.total_items_quantity.toString()
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

    fun emptyBottomFields() {
        bottom_P_id.value = ""
        bottom_P_name.value = ""
        bottom_P_img.value = ""
        bottom_packaging_purchase_price.value = "0.0"
        bottom_packaging_selling_price.value = "0.0"
        bottom_item_purchase_price.value = "0.0"
        bottom_item_selling_price.value = "0.0"
        bottom_P_barcode.value = ""
        bottom_P_category.value = ""
        bottom_P_description.value = ""
        bottom_P_packaging.value = "1"
        bottom_available_quantity_amount.value = "0.0"
        bottom_P_total_package_quantity.value = "0"
        bottom_P_total_items_quantity.value = "0"
        bottom_P_purchasing_date.value = ""
        bottom_P_expiration_date_Y.value = ""
        bottom_P_expiration_date_M.value = ""
        bottom_P_expiration_date_D.value = ""
        bottom_P_production_date_Y.value = ""
        bottom_P_production_date_M.value = ""
        bottom_P_production_date_D.value = ""
    }

    private fun calculateProductDataFromBottomFields():Domain_Inventory{
        return Domain_Inventory(
            product_id = bottom_P_id.value!!.toInt(),
            product_name =bottom_P_name.value!!,
            product_image = bottom_P_img.value!!,
            packaging_purchase_price = bottom_packaging_purchase_price.value!!.toDouble(),
            packaging_selling_price = bottom_packaging_selling_price.value!!.toDouble(),
            item_purchase_price = bottom_item_purchase_price.value!!.toDouble(),
            item_selling_price = bottom_item_selling_price.value!!.toDouble(),
            barcode = bottom_P_barcode.value!!,
            category = bottom_P_category.value!!,
            description = bottom_P_description.value!!,
            packaging = bottom_P_packaging.value!!.toInt(),
            available_quantity_amount = bottom_available_quantity_amount.value!!.toDouble(),
            total_package_quantity = bottom_P_total_package_quantity.value!!.toInt(),
            total_items_quantity = bottom_P_total_items_quantity.value!!.toInt(),
            purchasing_date = bottom_P_purchasing_date.value!!,
            expiration_date = bottom_P_expiration_date_Y.value + "-" + bottom_P_expiration_date_M.value + "-" + bottom_P_expiration_date_D.value,
            production_date = bottom_P_production_date_Y.value + "-" + bottom_P_production_date_M.value + "-" + bottom_P_production_date_D.value
        )
    }

    fun isBarcodeAvailable(code:String):Boolean{
        return inventories.any{it.barcode == code}
    }

}

sealed class InventoriesEvents {
    object ClickBackBtn : InventoriesEvents()
    object ClickBarcodeBtn : InventoriesEvents()
    object HideKeyBoard : InventoriesEvents()
    data class FilterInventories(val input: String, val filterType: FilterType) :
        InventoriesEvents()
    data class SetBarcodeStatus(val detectorStatus: Detector_status) : InventoriesEvents()
    data class OpenEditBottomSheet(val product_id: Int, val position: Int) : InventoriesEvents()
    object CloseEditBottomSheet : InventoriesEvents()
    data class ClickableEnabled(val status:Boolean=true):InventoriesEvents()
    object EditBtnClicked:InventoriesEvents()
    object OpenImgChooser:InventoriesEvents()
    data class GetReturnedImgUri(val uri:Uri?=null):InventoriesEvents()
    data class SaveNewImg(val product_img:String):InventoriesEvents()
}

enum class FilterType { CATEGORY, NAME, BARCODE, NOTHING }