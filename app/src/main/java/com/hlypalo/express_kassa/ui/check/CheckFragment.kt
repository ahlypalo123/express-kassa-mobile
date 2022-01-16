package com.hlypalo.express_kassa.ui.check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.PaymentMethod
import com.hlypalo.express_kassa.data.model.SaveCheckRequest
import com.hlypalo.express_kassa.data.repository.CheckRepository
import kotlinx.android.synthetic.main.fragment_check.*

class CheckFragment : Fragment() {

    private val repo: CheckRepository by lazy { CheckRepository() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_payment_method_cash?.setOnClickListener {
            gatherInfo(PaymentMethod.CASH)
        }
        btn_payment_method_card?.setOnClickListener {
            gatherInfo(PaymentMethod.CARD)
        }
    }

    private fun gatherInfo(paymentMethod: PaymentMethod) {
        val last4 = input_check_customer_number?.text.toString().toIntOrNull()
        val name = input_check_customer_name?.text.toString()
        val discount = input_check_discount?.text.toString().toFloatOrNull()
        val req = SaveCheckRequest (
            discount = discount,
            customerLast4 = last4,
            customerName = name,
            paymentMethod = paymentMethod,
        )
        repo.saveCheck(req)
    }
}