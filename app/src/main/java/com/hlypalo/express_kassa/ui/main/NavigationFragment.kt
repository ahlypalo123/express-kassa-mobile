package com.hlypalo.express_kassa.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.repository.CheckRepository
import com.hlypalo.express_kassa.data.repository.MerchantRepository
import com.hlypalo.express_kassa.ui.activity.MainActivity
import com.hlypalo.express_kassa.ui.check.CheckHistoryFragment
import com.hlypalo.express_kassa.ui.product.ProductFragment
import com.hlypalo.express_kassa.ui.settings.*
import com.hlypalo.express_kassa.util.PREF_INTERFACE_TYPE_FREE_SALE
import com.hlypalo.express_kassa.util.PREF_TOKEN
import com.hlypalo.express_kassa.util.PREF_TUTORIAL_VIEWED
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_navigation.*
import kotlinx.android.synthetic.main.layout_drawer_header.*

class NavigationFragment : Fragment() {

    private var showcaseView: ShowcaseView? = null
    private var lastSelected: Int = R.id.navigation_home
    private val repo: MerchantRepository by lazy { MerchantRepository.instance }
    private val checkRepo: CheckRepository by lazy { CheckRepository() }
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == PREF_INTERFACE_TYPE_FREE_SALE) {
            setFragment(if (App.sharedPrefs.getBoolean(PREF_INTERFACE_TYPE_FREE_SALE, false)) {
                FreeSaleFragment()
            } else {
                LegacyMainFragment()
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("lastSelected", lastSelected)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        savedInstanceState?.getInt("lastSelected")?.let {
            lastSelected = it
        }
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupHeader()

        if (!App.sharedPrefs.getBoolean(PREF_TUTORIAL_VIEWED, false)) {
            App.prefEditor.putBoolean(PREF_TUTORIAL_VIEWED, true).commit()
            showTutorial()
        }

        if (lastSelected != 0) {
            navigation?.setCheckedItem(lastSelected)
            selectItem(lastSelected)
        }

        navigation?.setNavigationItemSelectedListener func@{ item ->
            selectItem(item.itemId)
            return@func true
        }

        repo.initTemplate {
            activity?.showError(it)
        }

        App.sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onDestroyView() {
        App.sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
        super.onDestroyView()
    }

    private fun selectItem(@IdRes id: Int) {
        lastSelected = id
        when (id) {
            R.id.navigation_products -> {
                ProductFragment()
            }
            R.id.navigation_home -> {
                if (App.sharedPrefs.getBoolean(PREF_INTERFACE_TYPE_FREE_SALE, false)) {
                    FreeSaleFragment()
                } else {
                    LegacyMainFragment()
                }
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
            R.id.navigation_receipt -> {
                TemplatesFragment()
            }
            R.id.navigation_settings -> {
                MerchantDetailsFragment()
            }
            else -> {
                LegacyMainFragment()
            }
        }?.let {
            setFragment(it)
        }
        layout_navigation?.closeDrawers()
    }

    private fun showTutorial() {
        showcaseView = ShowcaseView.Builder(activity)
            .setTarget(ViewTarget(R.id.placeholder, activity))
            .setContentText("Формируйте список товаров, открывайте смену, добавляйте информацию о магазине в боковом меню")
            .hideOnTouchOutside()
            .setStyle(R.style.ShowcaseCustomTheme)
            .build().apply {
                hideButton()
            }
    }

    private fun setupHeader() {
        // = with(navigation.getHeaderView(0))
        navigation.getHeaderView(0).setOnClickListener {
            selectItem(R.id.navigation_settings)
        }
        repo.initMerchantDetails().observe(viewLifecycleOwner) {
            text_name?.text = it?.name
            text_address?.text = it?.address
            text_employee?.text = "Работник: ${it?.shift?.employeeName}"
            text_name?.visibility = if (it?.name.isNullOrBlank()) View.GONE else View.VISIBLE
            text_address?.visibility = if (it?.address.isNullOrBlank()) View.GONE else View.VISIBLE
            text_employee?.visibility = if (it?.shift?.employeeName.isNullOrBlank()) View.GONE else View.VISIBLE
        }
    }

    fun openDrawer() {
        showcaseView?.hide()
        layout_navigation?.openDrawer(GravityCompat.START)
    }

    private fun logout() {
        App.prefEditor.putString(PREF_TOKEN, null).commit()
        context?.let {
            startActivity(MainActivity.getStartIntent(it))
        }
        repo.clearCache()
        checkRepo.clearCache()
        activity?.finish()
    }

    private fun setFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.content_navigation, fragment, fragment::class.simpleName)
            .addToBackStack(fragment::class.simpleName).commit()
    }

}