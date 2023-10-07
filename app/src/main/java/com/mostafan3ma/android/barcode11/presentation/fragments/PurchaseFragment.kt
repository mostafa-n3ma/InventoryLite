package com.mostafan3ma.android.barcode11.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
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
import com.mostafan3ma.android.barcode11.R
import com.mostafan3ma.android.barcode11.databinding.FragmentPurchaseBinding
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.oporations.utils.hideKeyboard
import com.mostafan3ma.android.barcode11.oporations.utils.isAllPermissionsGranted
import com.mostafan3ma.android.barcode11.oporations.utils.requestPermissions
import com.mostafan3ma.android.barcode11.presentation.adapters.Operation
import com.mostafan3ma.android.barcode11.presentation.adapters.ReceiptAdapter
import com.mostafan3ma.android.barcode11.presentation.adapters.ReceiptListener
import com.mostafan3ma.android.barcode11.presentation.viewModels.Detector_status
import com.mostafan3ma.android.barcode11.presentation.viewModels.PurchaseViewModel
import com.mostafan3ma.android.barcode11.presentation.viewModels.Search_type
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PurchaseFragment : Fragment() {


    private lateinit var binding: FragmentPurchaseBinding
    private lateinit var addProductBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var productsAdapter: ReceiptAdapter


    val viewModel: PurchaseViewModel by viewModels()

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val TAG = "PurchaseFragment"
        var detectionCounter = 0
    }

    private lateinit var permissionsRequest: ActivityResultLauncher<Array<String>>

    // barcode detector & camera
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPurchaseBinding.inflate(layoutInflater)
        productsAdapter = ReceiptAdapter(ReceiptListener {id: Int,position:Int,operation: Operation ->
            Toast.makeText(
                requireContext(),
                "id:$id, position:$position, Operation:$operation",
                Toast.LENGTH_SHORT
            ).show()
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.adapter = productsAdapter
        addProductBottomSheetBehavior = setUpBottomSheet()

        subscribeObservers()
        permissionsRequest = getPermissionsRequest()



        return binding.root
    }


    private fun setUpBottomSheet(): BottomSheetBehavior<LinearLayout> {
        return BottomSheetBehavior.from(binding.addProductBottomSheet).apply {
            isDraggable = false
        }
    }


    private fun subscribeObservers() {
        viewModel.barcodeBtnClicked.observe(viewLifecycleOwner, Observer { clicked ->
            if (clicked) {
                requestPermissions(permissionsRequest, PERMISSIONS)
            }
        })
        viewModel.bottomSheetStatus.observe(viewLifecycleOwner, Observer { openBottomSheet ->
            if (openBottomSheet) {
                addProductBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.mainCard.visibility = View.GONE
                // set the card visibility = Gone so that the clicking on search field and barcode button stop
            } else {
                addProductBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.mainCard.visibility = View.VISIBLE
                if (binding.hiddenBarcodeCard.visibility == View.VISIBLE){
                    viewModel.setEvent(PurchaseViewModel.PurchaseViewModelEvent.SetBarcodeDetectorStatus(Detector_status.Receive))
                }
            }
        })
        viewModel.namesSuggestions.observe(viewLifecycleOwner, Observer {
            if (it == null) {

            }
            else {
                val suggestions: MutableList<String> = it
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    suggestions
                )
                adapter.add("Add New Product")
                binding.searchProductsEdit.setAdapter(adapter)


                binding.searchProductsEdit.setOnItemClickListener { _, _, position, _ ->
                    val selectedText = adapter.getItem(position).toString()
                    // Handle the selected item (e.g., show a message)
                    Toast.makeText(requireContext(), selectedText, Toast.LENGTH_SHORT).show()
                    when (selectedText) {
                        "Add New Product" -> {
                            viewModel.setEvent(
                                PurchaseViewModel.PurchaseViewModelEvent.OpenBottomSheetEvent(
                                    Domain_Inventory()
                                )
                            )
                        }
                        else -> {
                            val selectedProduct: Domain_Inventory? =
                                viewModel.getProductObject(Search_type.Text, selectedText)
                            viewModel.setEvent(
                                PurchaseViewModel.PurchaseViewModelEvent.OpenBottomSheetEvent(
                                    selectedProduct!!
                                )
                            )
                        }
                    }

                }


            }
        })
        viewModel.hideKeyBoardRequired.observe(
            viewLifecycleOwner,
            Observer { hideKeyBoardRequired ->
                if (hideKeyBoardRequired) {
                    hideKeyboard()
                }

            })

        viewModel.toast_msg.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.localProductsList.observe(viewLifecycleOwner, Observer {
            productsAdapter.submitList(it)
            Log.d(TAG, "subscribeObservers: localProductsList :adapter List ${productsAdapter.currentList}")
        })


        viewModel.backBtnClicked.observe(viewLifecycleOwner,Observer{goBack->
            if (goBack){
                findNavController().popBackStack()
            }
        })

        viewModel.clickDoneBtn.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                Toast.makeText(requireContext(),"Done Clicked",Toast.LENGTH_SHORT).show()
                viewModel.done()
            }
        })
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
            viewModel.setEvent(PurchaseViewModel.PurchaseViewModelEvent.SetBarcodeDetectorStatus(Detector_status.Receive))
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
            viewModel.setEvent(PurchaseViewModel.PurchaseViewModelEvent.SetBarcodeDetectorStatus(Detector_status.Pause))
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
                    Log.d(TAG, "receiveDetections: general ${code.displayValue}")
                    when(viewModel.openBarcodeDetection.value){
                        true->{
                            if (viewModel.isBarcodeAvailable(code.displayValue)){
                                Log.d(
                                    TAG,
                                    "receiveDetections: barcode detected and available :${code.displayValue}"
                                )
                                lifecycleScope.launch {

                                    viewModel.setEvent(
                                        PurchaseViewModel
                                            .PurchaseViewModelEvent
                                            .SetBarcodeDetectorStatus(Detector_status.Pause))//false stop detecting

                                    val detectedProduct: Domain_Inventory? = viewModel.getProductObject(Search_type.Barcode,code.displayValue)
                                    if (detectedProduct != null){
                                        detectedProduct.total_items_quantity = 0
                                        detectedProduct.total_package_quantity = 0
                                        viewModel.setEvent(PurchaseViewModel.PurchaseViewModelEvent.OpenBottomSheetEvent(detectedProduct))
                                    }

                                }



                            }
                            else{
                                //barcode is not recognized from database ...!
                                //check if it is recently added to localProductList..? and not yet saved to database ...?
                                Log.d(
                                    TAG,
                                    "receiveDetections: barcode is not recognized from database"
                                )
                                if (viewModel.localProductsList.value!!.any { it.barcode == code.displayValue }){
                                    detectionCounter++
                                    if (detectionCounter>50){
                                        Log.d(TAG, "receiveDetections: product is in localProductList")
                                        val localProduct: Domain_Inventory? = viewModel.localProductsList.value!!.find { it.barcode == code.displayValue }
                                        lifecycleScope.launch {
                                            viewModel.setEvent(PurchaseViewModel.PurchaseViewModelEvent.OpenBottomSheetEvent(localProduct!!))
                                            viewModel.setEvent(
                                                PurchaseViewModel
                                                    .PurchaseViewModelEvent
                                                    .SetBarcodeDetectorStatus(Detector_status.Pause))//false stop detecting
                                        }
                                        detectionCounter = 0
                                    }

                                }else{
                                    //not added to localProductList..? and not yet saved to database ...?
                                    // open empty bottomSheet with the detected barcode only
                                    detectionCounter++
                                    Log.d(TAG, "receiveDetections: counter =$detectionCounter")
                                    if(detectionCounter>100){
                                        Log.d(
                                            TAG,
                                            "receiveDetections: counter > 101 opening empty bottomSheet"
                                        )

                                        lifecycleScope.launch {
                                            // the lifecycleScope because it cannot invoke set value method like set event or setting values to the liveData properties of the view model from background thread
                                            viewModel.setEvent(PurchaseViewModel.PurchaseViewModelEvent.Toast_Announcement("Product is not registered creating new product"))
                                            viewModel.setEvent(PurchaseViewModel.PurchaseViewModelEvent.OpenBottomSheetEvent(Domain_Inventory(barcode = code.displayValue)))
                                            viewModel.setEvent(
                                                PurchaseViewModel
                                                    .PurchaseViewModelEvent
                                                    .SetBarcodeDetectorStatus(Detector_status.Pause))//false stop detecting
                                        }
                                        detectionCounter = 0

                                    }
                                }

                            }

                        }
                        false -> {
                            // do nothing...!
                        }
                        else -> {
                            // do nothing...!
                        }
                    }

                } else {
                    Log.d(TAG, "receiveDetections: Nothing detected - last")
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


}