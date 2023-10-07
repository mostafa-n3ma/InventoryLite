package com.mostafan3ma.android.barcode11.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mostafan3ma.android.barcode11.databinding.FragmentDashboardBinding
import com.mostafan3ma.android.barcode11.presentation.viewModels.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    lateinit var binding : FragmentDashboardBinding

     val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDashboardBinding.inflate(layoutInflater)
        binding.viewModel=viewModel
        binding.lifecycleOwner=this.viewLifecycleOwner

        subscribeObservers()

        return binding.root
    }

    private fun subscribeObservers() {
        viewModel.clickInventoryCard.observe(viewLifecycleOwner, Observer { inventoryClicked->
            if (inventoryClicked){
                Toast.makeText(requireContext(),"inventory card clicked",Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.clickSellReceiptCard.observe(viewLifecycleOwner, Observer { cicked->
            if (cicked){
                findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToSellFragment())
            }
        })


        viewModel.clickAnalyticsCard.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                Toast.makeText(requireContext(),"Analytics Card clicked",Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.clickAddProductsCard.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToPurchaseFragment())
            }
        })

        viewModel.clickTodaySalesCard.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                Toast.makeText(requireContext()," Today Sales Card  clicked",Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.clickRunOffProductsCard.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                Toast.makeText(requireContext()," Run Off Products Card  clicked",Toast.LENGTH_SHORT).show()
            }
        })

    }

}