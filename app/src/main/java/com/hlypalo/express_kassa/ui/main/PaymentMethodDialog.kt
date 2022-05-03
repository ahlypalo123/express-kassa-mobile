package com.hlypalo.express_kassa.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.PaymentMethod
import com.hlypalo.express_kassa.data.repository.CheckRepository
import com.hlypalo.express_kassa.data.repository.ProductRepository
import kotlinx.android.synthetic.main.dialog_payment_method.*

class PaymentMethodDialog(
    private val delegate: MainView
) : DialogFragment() {

    private val repo: CheckRepository by lazy { CheckRepository() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_payment_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_cash?.setOnClickListener {
            updatePaymentMethod(PaymentMethod.CASH)
            delegate.openCheckFragment()
            dialog?.dismiss()
        }
        btn_card?.setOnClickListener {
            updatePaymentMethod(PaymentMethod.CARD)
            delegate.openCheckFragment()
            dialog?.dismiss()
        }
        btn_cash_with_change?.setOnClickListener {
            updatePaymentMethod(PaymentMethod.CASH)
            delegate.showChangeDialog()
            dialog?.dismiss()
        }
    }

    private fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        val check = repo.getCheck()
        check?.paymentMethod = paymentMethod
        if (paymentMethod == PaymentMethod.CASH) {
            check?.cash = check?.total
        }
        repo.updateCheck(check)
    }

}