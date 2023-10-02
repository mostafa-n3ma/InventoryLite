package com.mostafan3ma.android.barcode11.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.mostafan3ma.android.barcode11.oporations.data_Mangment.repository.ShopRepository
import com.mostafan3ma.android.barcode11.presentation.fragments.DashboardFragment
import javax.inject.Inject

class FragmentFactory
@Inject
constructor(
    private val repository: ShopRepository
)
    : FragmentFactory()
{
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            DashboardFragment::class.java.name->{
                DashboardFragment()
            }

            else->{
                super.instantiate(classLoader, className)
            }

        }
    }
}