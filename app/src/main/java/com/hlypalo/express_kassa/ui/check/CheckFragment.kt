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
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.util.calculateTotal
import kotlinx.android.synthetic.main.fragment_check.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckFragment : Fragment() {

    private val repo: CheckRepository by lazy { CheckRepository() }
    private val productRepo: ProductRepository by lazy { ProductRepository() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_payment_method_cash?.setOnClickListener {
            saveCheck(PaymentMethod.CASH)
        }
        btn_payment_method_card?.setOnClickListener {
            saveCheck(PaymentMethod.CARD)
        }
        updateTotal()
    }

    private fun updateTotal() = CoroutineScope(Dispatchers.IO).launch {
        text_check_amount?.text = productRepo.getProductsFromCart().calculateTotal().toString()
    }

    private fun saveCheck(paymentMethod: PaymentMethod) {
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
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.content_navigation, CompleteFragment())
            ?.addToBackStack(null)?.commit()
    }
}