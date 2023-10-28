package com.mostafan3ma.android.barcode11.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.mostafan3ma.android.barcode11.operations.data_Management.repository.ShopRepository
import com.mostafan3ma.android.barcode11.operations.utils.SuperImageController
import com.mostafan3ma.android.barcode11.presentation.fragments.*
import javax.inject.Inject

class FragmentFactory
@Inject
constructor(
    private val repository: ShopRepository,
    private val supperImageController: SuperImageController
)
    : FragmentFactory()
{
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            DashboardFragment::class.java.name->{
                DashboardFragment()
            }
            PurchaseFragment::class.java.name->{
                PurchaseFragment(supperImageController)
            }
            SellFragment::class.java.name->{
                SellFragment()
            }
            InventoriesFragment::class.java.name->{
                InventoriesFragment(supperImageController)
            }
            AnalyticsFragment::class.java.name->
                AnalyticsFragment(supperImageController)

            else->{
                super.instantiate(classLoader, className)
            }

        }
    }
}