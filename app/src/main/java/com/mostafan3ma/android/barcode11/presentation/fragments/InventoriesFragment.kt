package com.mostafan3ma.android.barcode11.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.chip.Chip
import com.mostafan3ma.android.barcode11.R
import com.mostafan3ma.android.barcode11.databinding.FragmentInventoriesBinding
import com.mostafan3ma.android.barcode11.oporations.data_Entities.Domain_Inventory
import com.mostafan3ma.android.barcode11.oporations.utils.isAllPermissionsGranted
import com.mostafan3ma.android.barcode11.oporations.utils.requestPermissions
import com.mostafan3ma.android.barcode11.presentation.adapters.InventoriesAdapter
import com.mostafan3ma.android.barcode11.presentation.adapters.InventoriesListener
import com.mostafan3ma.android.barcode11.presentation.viewModels.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InventoriesFragment :Fragment() {

    lateinit var binding: FragmentInventoriesBinding
    private lateinit var inventoriesAdapter :InventoriesAdapter
     val viewModel:InventoriesViewModel by viewModels()
    companion object{
        const val TAG = "InventoriesFragment"
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
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
        binding = FragmentInventoriesBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        inventoriesAdapter = InventoriesAdapter(InventoriesListener{id: Int, position: Int ->
            Toast.makeText(requireContext(),"$id in $position clicked",Toast.LENGTH_SHORT).show()
        })
        binding.adapter = inventoriesAdapter



        permissionsRequest = getPermissionsRequest()
        subscribeObservers()
        return binding.root
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

    private fun subscribeObservers() {
        viewModel.categoriesList.observe(viewLifecycleOwner, Observer { categories->
            setUpCategoriesChips(categories)
        })
        viewModel.filteredList.observe(viewLifecycleOwner, Observer { filteredList->
            Log.d(TAG, "subscribeObservers: filteredList:$filteredList")
            if (filteredList !=null){
                inventoriesAdapter.submitList(filteredList)
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

    }

}