package com.mostafan3ma.android.barcode11.presentation.fragments

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mostafan3ma.android.barcode11.databinding.FragmentAnalyticsBinding
import com.mostafan3ma.android.barcode11.oporations.utils.SuperImageController
import com.mostafan3ma.android.barcode11.presentation.adapters.AnalyticsAdapter_E
import com.mostafan3ma.android.barcode11.presentation.adapters.AnalyticsAdapter_Q
import com.mostafan3ma.android.barcode11.presentation.adapters.AnalyticsAdapter_S
import com.mostafan3ma.android.barcode11.presentation.adapters.Analytics_Adapter_R
import com.mostafan3ma.android.barcode11.presentation.viewModels.AnalyticsEvent
import com.mostafan3ma.android.barcode11.presentation.viewModels.AnalyticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalyticsFragment
constructor(private val superImageController: SuperImageController) : Fragment() {
    lateinit var binding: FragmentAnalyticsBinding

    val viewModel:AnalyticsViewModel by viewModels()

    private lateinit var quantitiesAdapter:AnalyticsAdapter_Q
    private lateinit var salesAdapter:AnalyticsAdapter_S
    private lateinit var expiryAdapter:AnalyticsAdapter_E
    private lateinit var sellReceiptsAdapter:Analytics_Adapter_R
    private lateinit var purchaseReceiptsAdapter:Analytics_Adapter_R

    companion object{
        const val TAG = "AnalyticsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        quantitiesAdapter = AnalyticsAdapter_Q(requireContext())
        binding.quantitiesAdapter = quantitiesAdapter
        salesAdapter = AnalyticsAdapter_S(requireContext())
        binding.salesAdapter = salesAdapter
        expiryAdapter = AnalyticsAdapter_E(requireContext())
        binding.expiryAdapter = expiryAdapter
        sellReceiptsAdapter = Analytics_Adapter_R()
        binding.sellReceiptsAdapter = sellReceiptsAdapter
        purchaseReceiptsAdapter = Analytics_Adapter_R()
        binding.purchaseReceiptsAdapter = purchaseReceiptsAdapter



        declareClickEvents()



        subscribeObservers()
        return binding.root
    }

    private fun declareClickEvents() {
        binding.clickBackEvent = AnalyticsEvent.ClickBackBtnEvent
        binding.clickQuantitiesCardEvent = AnalyticsEvent.ClickQuantitiesCardEvent
        binding.clickSalesCardEvent = AnalyticsEvent.ClickSalesCardEvent
        binding.clickExpiryCardEvent = AnalyticsEvent.ClickExpiryCardEvent
        binding.clickSellReceiptsCardEvent = AnalyticsEvent.ClickSellReceiptsCardEvent
        binding.clickPurchaseReceiptsCardEvent = AnalyticsEvent.ClickPurchaseReceiptsCardEvent

    }

    private fun subscribeObservers() {
        //click events
        viewModel.backBtnClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                findNavController().navigate(AnalyticsFragmentDirections.actionAnalyticsFragmentToDashboardFragment())
            }
        })
        viewModel.quantitiesCardClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                checkQuantitiesCard()
            }
        })
        viewModel.salesCardClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                checkProductsSalesCard()
            }
        })
        viewModel.expiryCardClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                checkExpiryCard()
            }
        })
        viewModel.sellReceiptsCardClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                checkSellReceiptsCard()
            }
        })
        viewModel.purchasedReceiptsCardClicked.observe(viewLifecycleOwner, Observer { clicked->
            if (clicked){
                checkPurchaseReceiptsCard()
            }
        })

        // data observing
        viewModel.quantitiesList.observe(viewLifecycleOwner, Observer { quantities->
            Log.d(TAG, "subscribeObservers: quantities List : $quantities")
            quantitiesAdapter.submitList(quantities)

        })

        viewModel.salesList.observe(viewLifecycleOwner, Observer { sales->
            Log.d(TAG, "subscribeObservers: Sales List:$sales")
            salesAdapter.submitList(sales)
        })


        viewModel.expiryList.observe(viewLifecycleOwner, Observer { expiryDates->
            Log.d(TAG, "subscribeObservers: expiry dates : $expiryDates")
            expiryAdapter.submitList(expiryDates)
        })

        viewModel.sellReceiptsList.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "subscribeObservers: SellReceiptList : $it")
            sellReceiptsAdapter.submitList(it.asReversed())

        })

        viewModel.purchaseReceiptsList.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "subscribeObservers: SellReceiptList : $it")
            purchaseReceiptsAdapter.submitList(it.asReversed())
        })





    }


    private fun checkPurchaseReceiptsCard() {
        if (binding.purchasedReceiptRecLayout.visibility == View.GONE){
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.purchasedReceiptRecLayout.visibility = View.VISIBLE
            binding.purchasedReceiptDescriptionLayout.visibility = View.GONE

            binding.totalProfitCard .visibility = View.GONE
            binding.totalExpensesCard.visibility = View.GONE
            binding.quantitiesCard.visibility = View.GONE
            binding.productsSalesCard.visibility = View.GONE
            binding.expiryCard.visibility = View.GONE
            binding.sellReceiptCard.visibility = View.GONE

        }else{
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.purchasedReceiptRecLayout.visibility = View.GONE
            binding.purchasedReceiptDescriptionLayout.visibility = View.VISIBLE

            binding.totalProfitCard .visibility = View.VISIBLE
            binding.totalExpensesCard.visibility = View.VISIBLE
            binding.quantitiesCard.visibility = View.VISIBLE
            binding.productsSalesCard.visibility = View.VISIBLE
            binding.expiryCard.visibility = View.VISIBLE
            binding.sellReceiptCard.visibility = View.VISIBLE
        }

    }

    private fun checkSellReceiptsCard() {
        if (binding.sellReceiptRecLayout.visibility == View.GONE){
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.sellReceiptRecLayout.visibility = View.VISIBLE
            binding.sellReceiptDescriptionLayout.visibility = View.GONE

            binding.totalProfitCard .visibility = View.GONE
            binding.totalExpensesCard.visibility = View.GONE
            binding.quantitiesCard.visibility = View.GONE
            binding.productsSalesCard.visibility = View.GONE
            binding.expiryCard.visibility = View.GONE
            binding.purchasedReceiptCard.visibility = View.GONE

        }else{
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.sellReceiptRecLayout.visibility = View.GONE
            binding.sellReceiptDescriptionLayout.visibility = View.VISIBLE

            binding.totalProfitCard .visibility = View.VISIBLE
            binding.totalExpensesCard.visibility = View.VISIBLE
            binding.quantitiesCard.visibility = View.VISIBLE
            binding.productsSalesCard.visibility = View.VISIBLE
            binding.expiryCard.visibility = View.VISIBLE
            binding.purchasedReceiptCard.visibility = View.VISIBLE
        }

    }

    private fun checkExpiryCard() {
        if (binding.expiryRecLayout.visibility == View.GONE){
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.expiryRecLayout.visibility = View.VISIBLE
            binding.expiryHiddenTag.visibility = View.VISIBLE
            binding.expiryDescriptionLayout.visibility = View.GONE

            binding.totalProfitCard .visibility = View.GONE
            binding.totalExpensesCard.visibility = View.GONE
            binding.quantitiesCard.visibility = View.GONE
            binding.productsSalesCard.visibility = View.GONE
            binding.sellReceiptCard.visibility = View.GONE
            binding.purchasedReceiptCard.visibility = View.GONE

        }else{
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.expiryRecLayout.visibility = View.GONE
            binding.expiryHiddenTag.visibility = View.GONE
            binding.expiryDescriptionLayout.visibility = View.VISIBLE

            binding.totalProfitCard .visibility = View.VISIBLE
            binding.totalExpensesCard.visibility = View.VISIBLE
            binding.quantitiesCard.visibility = View.VISIBLE
            binding.productsSalesCard.visibility = View.VISIBLE
            binding.sellReceiptCard.visibility = View.VISIBLE
            binding.purchasedReceiptCard.visibility = View.VISIBLE
        }

    }

    private fun checkQuantitiesCard() {
        if (binding.quantitiesRecLayout.visibility == View.GONE){
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.quantitiesRecLayout.visibility = View.VISIBLE
            binding.quantitiesHiddenTag.visibility = View.VISIBLE

            binding.quantitiesDescriptionLayout.visibility = View.GONE
            binding.totalProfitCard .visibility = View.GONE
            binding.totalExpensesCard.visibility = View.GONE
            binding.productsSalesCard.visibility = View.GONE
            binding.expiryCard.visibility = View.GONE
            binding.sellReceiptCard.visibility = View.GONE
            binding.purchasedReceiptCard.visibility = View.GONE

        }else{
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.quantitiesRecLayout.visibility = View.GONE
            binding.quantitiesHiddenTag.visibility = View.GONE

            binding.quantitiesDescriptionLayout.visibility = View.VISIBLE
            binding.totalProfitCard .visibility = View.VISIBLE
            binding.totalExpensesCard.visibility = View.VISIBLE
            binding.productsSalesCard.visibility = View.VISIBLE
            binding.expiryCard.visibility = View.VISIBLE
            binding.sellReceiptCard.visibility = View.VISIBLE
            binding.purchasedReceiptCard.visibility = View.VISIBLE
        }
    }

    private fun checkProductsSalesCard() {
        if (binding.productsSalesRecLayout.visibility == View.GONE){
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.productsSalesRecLayout.visibility = View.VISIBLE
            binding.productsSalesHiddenTag.visibility = View.VISIBLE
            binding.productsSalesDescriptionLayout.visibility = View.GONE

            binding.totalProfitCard .visibility = View.GONE
            binding.totalExpensesCard.visibility = View.GONE
            binding.quantitiesCard.visibility = View.GONE
            binding.expiryCard.visibility = View.GONE
            binding.sellReceiptCard.visibility = View.GONE
            binding.purchasedReceiptCard.visibility = View.GONE

        }else{
            TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
            binding.productsSalesRecLayout.visibility = View.GONE
            binding.productsSalesHiddenTag.visibility = View.GONE
            binding.productsSalesDescriptionLayout.visibility = View.VISIBLE

            binding.totalProfitCard .visibility = View.VISIBLE
            binding.totalExpensesCard.visibility = View.VISIBLE
            binding.quantitiesCard.visibility = View.VISIBLE
            binding.expiryCard.visibility = View.VISIBLE
            binding.sellReceiptCard.visibility = View.VISIBLE
            binding.purchasedReceiptCard.visibility = View.VISIBLE
        }
    }


}