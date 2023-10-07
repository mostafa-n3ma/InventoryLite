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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.util.isNotEmpty
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.mostafan3ma.android.barcode11.R
import com.mostafan3ma.android.barcode11.databinding.FragmentSellBinding
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.oporations.utils.hideKeyboard
import com.mostafan3ma.android.barcode11.oporations.utils.isAllPermissionsGranted
import com.mostafan3ma.android.barcode11.oporations.utils.requestPermissions
import com.mostafan3ma.android.barcode11.presentation.adapters.Operation
import com.mostafan3ma.android.barcode11.presentation.adapters.ReceiptAdapter
import com.mostafan3ma.android.barcode11.presentation.adapters.ReceiptListener
import com.mostafan3ma.android.barcode11.presentation.viewModels.*
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll
import okhttp3.internal.toImmutableList
import kotlin.math.log


@AndroidEntryPoint
class SellFragment : Fragment() {

    val viewModel: SellViewModel by viewModels()
    private lateinit var binding: FragmentSellBinding
    private lateinit var productsAdapter: ReceiptAdapter



    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val TAG = "SellFragment"
    }

    private lateinit var permissionsRequest: ActivityResultLauncher<Array<String>>
    // barcode detector & camera
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        productsAdapter = ReceiptAdapter(ReceiptListener { id: Int,position:Int,operation: Operation->
            Toast.makeText(
                requireContext(),
                "id:$id, position:$position, Operation:$operation",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.setEvent(SellViewModelEvent.UpdateItem(id,position, operation))
        })
        binding.adapter = productsAdapter
        permissionsRequest = getPermissionsRequest()

        subscribeObservers()


        return binding.root
    }

    private fun subscribeObservers() {
        viewModel.barcodeBtnClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                requestPermissions(permissionsRequest, PERMISSIONS)
            }
        })
        viewModel.namesSuggestions.observe(viewLifecycleOwner, Observer {
            if (it !=null){
                val suggestions: MutableList<String> = it
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    suggestions
                )
                binding.searchProductsEdit.setAdapter(adapter)
                binding.searchProductsEdit.setOnItemClickListener { _, _, position, _ ->
                    val selectedText = adapter.getItem(position).toString()
                    viewModel.setEvent(SellViewModelEvent.UpdateReceiptList(selectedText,InputType.TEXT))
                    val list: MutableList<Domain_Inventory> = productsAdapter.currentList
                    val index = list.indexOf(list.find { it.product_name == selectedText })
                    viewModel.setEvent(SellViewModelEvent.NotifyAdapter(index))
                }

            }
        })
        viewModel.receiptList.observe(viewLifecycleOwner, Observer { receiptList->
            productsAdapter.submitList(receiptList.asReversed())
        })
        viewModel.hideKeyBoardRequired.observe(viewLifecycleOwner, Observer {
            if (it){
                hideKeyboard()
            }
        })
        viewModel.backBtnClicked.observe(viewLifecycleOwner, Observer {clicked->
            if (clicked){
                findNavController().navigate(SellFragmentDirections.actionSellFragmentToDashboardFragment())
            }
        })
        viewModel.notifyPosition.observe(viewLifecycleOwner, Observer {position->
            if (position!=null){
                productsAdapter.notifyItemChanged(position)
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
            viewModel.setEvent(SellViewModelEvent.SetBarcodeDetectorStatus(Detector_status.Receive))
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
            viewModel.setEvent(SellViewModelEvent.SetBarcodeDetectorStatus(Detector_status.Pause))
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
                    when(viewModel.detectorOpened.value){
                        true -> {
                            lifecycleScope.launch {
                                viewModel.setEvent(SellViewModelEvent.UpdateReceiptList(code.displayValue,InputType.BARCODE))
                                val list: MutableList<Domain_Inventory> = productsAdapter.currentList
                                val index = list.indexOf(list.find { it.barcode ==  code.displayValue})
                                viewModel.setEvent(SellViewModelEvent.NotifyAdapter(index))
                            }
                        }
                        false -> {
                            Log.d(
                                TAG,
                                "receiveDetections: detecting : ${code.displayValue} detection closed /status :Pause"
                            )
                        }
                        null -> {
                        // nothing
                        }
                    }


                } else {
                    Log.d(TAG, "receiveDetections: detection nothing")
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