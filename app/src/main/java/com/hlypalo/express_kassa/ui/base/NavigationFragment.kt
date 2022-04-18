package com.hlypalo.express_kassa.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.ui.activity.MainActivity
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.ui.check.CheckHistoryFragment
import com.hlypalo.express_kassa.ui.devices.PrintersFragment
import com.hlypalo.express_kassa.ui.main.MainFragment
import com.hlypalo.express_kassa.ui.merchant.MerchantDetailsFragment
import com.hlypalo.express_kassa.ui.product.ProductFragment
import com.hlypalo.express_kassa.ui.shift.ShiftFragment
import com.hlypalo.express_kassa.util.PREF_TOKEN
import kotlinx.android.synthetic.main.fragment_navigation.*

class NavigationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        pushFragment(MainFragment())

        navigation?.setNavigationItemSelectedListener func@{ item ->
            when (item.itemId) {
                R.id.navigation_products -> {
                    ProductFragment()
                }
                R.id.navigation_home -> {
                    MainFragment()
                }
                R.id.navigation_shift -> {
                    ShiftFragment()
                }
                R.id.navigation_log_out -> {
                    logout()
                    null
                }
                R.id.navigation_history -> {
                    CheckHistoryFragment()
                }
                R.id.navigation_devices -> {
                    PrintersFragment()
                }
                R.id.navigation_settings -> {
                    MerchantDetailsFragment()
                }
                else -> {
                    MainFragment()
                }
            }?.let {
                pushFragment(it)
            }
            layout_navigation?.closeDrawers()
            return@func true
        }
    }

    fun openDrawer() {
        layout_navigation?.openDrawer(GravityCompat.START)
    }

    private fun logout() {
        App.prefEditor.putString(PREF_TOKEN, null).commit()
        context?.let {
            startActivity(MainActivity.getStartIntent(it))
        }
        activity?.finish()
    }

    private fun pushFragment(fragment: Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.content_navigation, fragment)
            ?.commit()
    }

}