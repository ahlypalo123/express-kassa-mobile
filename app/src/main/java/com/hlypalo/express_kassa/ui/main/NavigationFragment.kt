package com.hlypalo.express_kassa.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.MainActivity
import com.hlypalo.express_kassa.R
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
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // activity?.supportActionBar?.setHomeAsUpIndicator()

        setHasOptionsMenu(true)

        pushFragment(MainFragment())

        navigation?.setNavigationItemSelectedListener func@{ item ->
            Toast.makeText(context, "You've selected $item", Toast.LENGTH_LONG).show()
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
            ?.addToBackStack(null)?.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                layout_navigation?.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}