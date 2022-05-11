package com.hlypalo.express_kassa.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.MerchantDetails
import com.hlypalo.express_kassa.data.repository.MerchantRepository
import com.hlypalo.express_kassa.ui.main.NavigationFragment
import com.hlypalo.express_kassa.ui.main.MainFragment
import com.hlypalo.express_kassa.util.confirm
import com.hlypalo.express_kassa.util.enqueue
import com.hlypalo.express_kassa.util.showCarouselDialog
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_merchant_details.*
import kotlinx.android.synthetic.main.fragment_merchant_details.toolbar

class MerchantDetailsFragment : Fragment() {

    private val repo: MerchantRepository by lazy { MerchantRepository.instance }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_merchant_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

        updateUi()

        btn_save?.setOnClickListener {
            update()
        }

        input_tax_type?.setOnFocusChangeListener { _, b ->
            if (b) {
                input_tax_type?.clearFocus()
                val taxTypes = arrayOf("Общее", "УСН", "ЕСХН", "ЕНВД", "ПСН", "НПД")
                activity?.showCarouselDialog(0, { taxTypes }, "Выберете тип налогообложения") {
                    input_tax_type?.setText(taxTypes[it])
                }
            }
        }
    }

    private fun update() {
        val req = MerchantDetails(
            id = 0,
            inn = input_inn?.text?.toString(),
            name = input_organization?.text?.toString(),
            taxType = input_tax_type?.text?.toString(),
            address = input_address?.text?.toString()
        )

        progress_bar?.visibility = View.VISIBLE
        repo.updateMerchantDetails(req, callback = {
            activity?.confirm("Настройки успешно обновлены", onYes = {
//                parentFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.content_navigation, MainFragment())
//                    .commit()
            }, yesText = "Ok")
            progress_bar?.visibility = View.INVISIBLE
        }, error = {
            activity?.showError(it)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val fragment = activity?.supportFragmentManager
                    ?.findFragmentByTag(NavigationFragment::class.java.simpleName)
                (fragment as? NavigationFragment)?.openDrawer()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUi() {
        repo.getMerchantDetails().let {
            input_inn?.setText(it?.inn)
            input_address?.setText(it?.address)
            input_organization?.setText(it?.name)
            input_tax_type?.setText(it?.taxType)
        }
    }

}