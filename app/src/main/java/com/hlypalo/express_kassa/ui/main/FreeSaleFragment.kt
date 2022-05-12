package com.hlypalo.express_kassa.ui.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CheckProduct
import com.hlypalo.express_kassa.data.model.ErrorBody
import com.hlypalo.express_kassa.data.model.PaymentMethod
import com.hlypalo.express_kassa.ui.check.CheckFragment
import com.hlypalo.express_kassa.util.PREF_INTERFACE_TYPE_FREE_SALE
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_free_sale.*
import kotlinx.android.synthetic.main.fragment_products.toolbar

class FreeSaleFragment : Fragment(), MainView {

    private val presenter: MainPresenter by lazy { MainPresenter(this) }
    private var change = 0F

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_free_sale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

        input_total?.addTextChangedListener {
            input_total?.error = null
            val total = getTotal()
            btn_check_pay?.text = "Оплатить $total"
            if (change == 0F) {
                input_cash?.setText(total.toString())
            }
        }

        input_cash?.addTextChangedListener {
            val cash = getCash()
            change = cash - getTotal()
            text_change?.text = "Сдача $change"
        }

        btn_check_pay?.setOnClickListener {
            pay()
        }

        radio_cash?.setOnCheckedChangeListener { _, b ->
            input_cash?.isEnabled = b
            if (!b) {
                input_cash?.setText(getTotal().toString())
            }
        }

        radio_cash?.isChecked = true

        presenter.init()
    }

    private fun pay() {
        val total = getTotal()

        if (total == 0F) {
            input_total?.error = "Пожалуйста, введите сумму продажи"
            return
        }

        presenter.check?.let { check ->
            check.cash = getCash()
            check.total = total
            check.paymentMethod = if (radio_cash?.isChecked == true) {
                PaymentMethod.CASH
            } else {
                PaymentMethod.CARD
            }
            check.products = mutableListOf(
                CheckProduct(
                    name = input_product_name?.text?.toString(),
                    price = total,
                    count = 1
                )
            )
            presenter.updateCheck(check)
        }
        presenter.onPay()
    }

    private fun getTotal() : Float = input_total?.text.toString().toFloatOrNull() ?: 0F
    private fun getCash() : Float = input_cash?.text.toString().toFloatOrNull() ?: 0F

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_free_sale, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (parentFragment as? NavigationFragment)?.openDrawer()
            }
            R.id.interface_list -> {
                App.prefEditor.putBoolean(PREF_INTERFACE_TYPE_FREE_SALE, false).commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun toggleProgress(f: Boolean) {
        progress_bar?.visibility = if (f) View.VISIBLE else View.GONE
    }

    override fun startPayment() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
            ?.replace(R.id.container, CheckFragment())
            ?.addToBackStack(null)?.commit()
    }

    override fun updateTotal(total: Float?) {
        input_total?.setText((total ?: 0).toString())
    }

    override fun showError(err: ErrorBody?) {
        activity?.showError(err)
    }

}