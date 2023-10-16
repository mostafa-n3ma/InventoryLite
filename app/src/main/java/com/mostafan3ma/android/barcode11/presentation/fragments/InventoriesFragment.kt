package com.mostafan3ma.android.barcode11.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.util.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.mostafan3ma.android.barcode11.R
import com.mostafan3ma.android.barcode11.databinding.FragmentInventoriesBinding
import com.mostafan3ma.android.barcode11.oporations.utils.SuperImageController
import com.mostafan3ma.android.barcode11.oporations.utils.hideKeyboard
import com.mostafan3ma.android.barcode11.oporations.utils.isAllPermissionsGranted
import com.mostafan3ma.android.barcode11.oporations.utils.requestPermissions
import com.mostafan3ma.android.barcode11.presentation.adapters.InventoriesAdapter
import com.mostafan3ma.android.barcode11.presentation.adapters.InventoriesListener
import com.mostafan3ma.android.barcode11.presentation.viewModels.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InventoriesFragment
@Inject
    constructor(private val superImageController: SuperImageController)
    :Fragment() {

    lateinit var binding: FragmentInventoriesBinding
    private lateinit var inventoriesAdapter :InventoriesAdapter
    private lateinit var editProductBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    val viewModel:InventoriesViewModel by viewModels()
    var clickableEnabled:Boolean = true
    companion object{
        const val TAG = "InventoriesFragment"
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private lateinit var permissionsRequest: ActivityResultLauncher<Array<String>>
    // barcode detector & camera
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        if (superImageController.returnedUri!=null){
            viewModel.setEvent(InventoriesEvents.GetReturnedImgUri(superImageController.returnedUri))
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInventoriesBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        inventoriesAdapter = InventoriesAdapter(InventoriesListener{id: Int, position: Int ->
            //click listener
            when(clickableEnabled){
                true -> Toast.makeText(requireContext(),"$id in $position clicked",Toast.LENGTH_SHORT).show()
                false -> Toast.makeText(requireContext(),"$id in $position clicked disabled",Toast.LENGTH_SHORT).show()
            }

        },InventoriesListener{id: Int, position: Int ->
            // long click listener
            when(clickableEnabled){
                true -> {
                    Toast.makeText(requireContext(),"long clicked item :$id , on position : $position",Toast.LENGTH_SHORT).show()
                    viewModel.setEvent(InventoriesEvents.OpenEditBottomSheet(id,position))
                }
                false -> Toast.makeText(requireContext(),"$id in $position clicked disabled",Toast.LENGTH_SHORT).show()
            }

        })
        binding.adapter = inventoriesAdapter


        editProductBottomSheetBehavior = setUpBottomSheet()
        superImageController.register(this)


        permissionsRequest = getPermissionsRequest()
        subscribeObservers()
        return binding.root
    }


    private fun setUpBottomSheet(): BottomSheetBehavior<LinearLayout> {
        return BottomSheetBehavior.from(binding.addProductBottomSheet).apply {
            isDraggable = false
        }
    }



    private fun getPermissionsRequest() =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (isAllPermissionsGranted(PERMISSIONS)) {
                Toast.makeText(requireContext(), "permission granted", Toast.LENGTH_SHORT).show()
                setUpBarcodeDetector()
                checkBarcodeScannerCard()
            } else {
                Toast.makeText(requireContext(), "permission not granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun checkBarcodeScannerCard() {
        if (binding.hiddenBarcodeCard.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding.mainCard, AutoTransition())
            binding.hiddenBarcodeCard.visibility = View.VISIBLE
            viewModel.setEvent(InventoriesEvents.SetBarcodeStatus(Detector_status.Receive))
            binding.searchProductsField.visibility = View.GONE
            binding.barcodeBtn.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.collapse,
                0,
                0,
                0
            )
            binding.barcodeBtn.text = "Search By Name"
        } else {
            TransitionManager.beginDelayedTransition(binding.mainCard, AutoTransition())
            binding.hiddenBarcodeCard.visibility = View.GONE
            viewModel.setEvent(InventoriesEvents.SetBarcodeStatus(Detector_status.Pause))
            binding.searchProductsField.visibility = View.VISIBLE
            binding.barcodeBtn.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.expend,
                0,
                0,
                0
            )
            binding.barcodeBtn.text = "Barcode"
        }
    }


    private fun setUpBarcodeDetector() {

        val surfaceCallback = object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                try {
                    cameraSource.start(surfaceHolder)
                } catch (exception: Exception) {
                    Toast.makeText(requireContext(), "something went wrong", Toast.LENGTH_LONG).show()
                }
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                cameraSource.stop()
            }
        }


        val processor = object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                if (detections.detectedItems.isNotEmpty()) {
                    val qrCodes: SparseArray<Barcode> = detections.detectedItems
                    val code = qrCodes.valueAt(0)
                    // if the barcode is available in data base stop detection ,close barcode card view
                    Log.d(TAG, "receiveDetections: ${code.displayValue}")
                    if (viewModel.detectorOpened.value!!){
                        if (viewModel.isBarcodeAvailable(code.displayValue)){
                            lifecycleScope.launch {
                                viewModel.setEvent(InventoriesEvents.FilterInventories(code.displayValue,FilterType.BARCODE))
                                viewModel.setEvent(InventoriesEvents.SetBarcodeStatus(Detector_status.Pause))
                                checkBarcodeScannerCard()
                            }
                        }
                    }

                } else {
                    Log.d(SellFragment.TAG, "receiveDetections: detection nothing")
                }
            }
        }





        detector = BarcodeDetector.Builder(requireContext()).build()
        cameraSource = CameraSource.Builder(requireContext(), detector)
            .setAutoFocusEnabled(true)
            .build()
        binding.barcodeScannerSurface.holder.addCallback(surfaceCallback)
        detector.setProcessor(processor)
    }

    private fun setUpCategoriesChips(categories: List<String>) {
        val chipsGroup = binding.categoriesChips
        val inflater = LayoutInflater.from(chipsGroup.context)
        Log.d(TAG, "setUpCategoriesChips: categories:${viewModel.categoriesList}")
        val childrenChips: List<Chip> = categories.map { chipName->
            val chip = inflater.inflate(
                R.layout.chip_category_item,
                chipsGroup,
                false
            ) as Chip
            chip.text = chipName
            chip.tag = chipName
            val states = arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_checked)
            )
            val colors = intArrayOf(Color.parseColor("#100101"), Color.parseColor("#FFFFFFFF"))
            val colorsStateList = ColorStateList(states, colors)
            chip.chipBackgroundColor = colorsStateList

            chip.setOnCheckedChangeListener { compoundButton, checked ->
                        if (checked) {
                            viewModel.setEvent(InventoriesEvents.FilterInventories(compoundButton.tag.toString(),FilterType.CATEGORY))
                            chip.chipBackgroundColor = colorsStateList
                            chip.setTextColor(Color.WHITE)
                        } else {
                            chip.chipBackgroundColor = colorsStateList
                            chip.setTextColor(Color.BLACK)
                        }

            }

            chip
        }
        chipsGroup.removeAllViews()
        for (chip in childrenChips) {
            chipsGroup.addView(chip)
        }
        chipsGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                viewModel.setEvent(InventoriesEvents.FilterInventories("",FilterType.NOTHING))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun subscribeObservers() {
        viewModel.categoriesList.observe(viewLifecycleOwner, Observer { categories->
            setUpCategoriesChips(categories)
            Log.d(TAG, "subscribeObservers: categoriesList:$categories")
        })
        viewModel.filteredList.observe(viewLifecycleOwner, Observer { filteredList->
//            Log.d(TAG, "subscribeObservers: filteredList:$filteredList")
            if (filteredList !=null){
                inventoriesAdapter.submitList(filteredList)
                Log.d(TAG, "subscribeObservers: filteredList:${filteredList.size}")
            }
        })
        viewModel.namesSuggestions.observe(viewLifecycleOwner, Observer {suggestions->
            if (suggestions!=null){
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    suggestions
                )
                binding.searchProductsEdit.setAdapter(adapter)
                binding.searchProductsEdit.setOnItemClickListener { _, _, position, _ ->
                    val selectedText = adapter.getItem(position).toString()
                    viewModel.setEvent(InventoriesEvents.FilterInventories(selectedText,FilterType.NAME))
                    viewModel.setEvent(InventoriesEvents.HideKeyBoard)
                }

            }
        })
        viewModel.barcodeBtnClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                requestPermissions(permissionsRequest, PERMISSIONS)
            }
        })
        viewModel.backBtnClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                findNavController().navigate(InventoriesFragmentDirections.actionInventoriesFragmentToDashboardFragment())
            }
        })
        viewModel.editBottomSheetOpened.observe(viewLifecycleOwner, Observer { opened->
            if (opened){
                editProductBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                Log.d(TAG, "subscribeObservers: editbottomSheet opened : true")
            }else{
                editProductBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                Log.d(TAG, "subscribeObservers: editbottomSheet opened : false")
            }
        })
        viewModel.clickable.observe(viewLifecycleOwner,Observer{status->
            clickableEnabled = status
        })
        viewModel.hideKeyBoardRequired.observe(viewLifecycleOwner,Observer{ hideKeyBoardRequired ->
            if (hideKeyBoardRequired) {
                hideKeyboard()
            }

        })
        viewModel.openImgChooser.observe(viewLifecycleOwner, Observer { openRequired->
            if (openRequired){
                superImageController.launchRegistrar()
            }

        })
        viewModel.returnedBottomImgUri.observe(viewLifecycleOwner,Observer{ imgUri->
            binding.bottomAddImgBtn.setImageURI(imgUri)
        })

        viewModel.saveNewImg.observe(viewLifecycleOwner, Observer { product_img->
            if (product_img !=null){
                val imgBitmap = superImageController.getBitmapFromRegister(requireContext())
                lifecycleScope.launch {
                    superImageController.saveImageToInternalStorage(requireContext(),imgBitmap,product_img)
                }

            }
        })
    }

}