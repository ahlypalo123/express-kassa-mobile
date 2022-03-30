package com.hlypalo.express_kassa.ui.check

import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.model.PaymentMethod
import com.hlypalo.express_kassa.data.repository.CheckRepository
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.util.*
import kotlinx.android.synthetic.main.fragment_check.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class CheckFragment : Fragment() {

    private val repo: CheckRepository by lazy { CheckRepository() }
    private val productRepo: ProductRepository by lazy { ProductRepository() }
    private var total: Float = 0.0f
    private val executor = Executors.newSingleThreadExecutor();

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
        total = productRepo.getProductsFromCartGrouped().calculateTotal()
        text_check_amount?.text = total.toString()
    }

    private fun saveCheck(paymentMethod: PaymentMethod) = CoroutineScope(Dispatchers.IO).launch {
        val last4 = input_check_customer_number?.text.toString().toIntOrNull()
        val name = input_check_customer_name?.text.toString()
        val discount = input_check_discount?.text.toString().toFloatOrNull()
        val req = Check (
            id = 0,
            discount = discount,
            customerLast4 = last4,
            customerName = name,
            paymentMethod = paymentMethod,
            date = System.currentTimeMillis(),
            products = productRepo.getProductsFromCart(),
            total = total,
            employeeName = App.sharedPrefs.getString(PREF_EMPLOYEE_NAME, "") ?: ""
        )
        val products = productRepo.getProductsFromCartGrouped()
        repo.saveCheck(req)
        productRepo.deleteProductsFromCart()
        withContext(Dispatchers.Main) {
            printCheck(req, products)
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_navigation, CompleteFragment())
                ?.addToBackStack(null)?.commit()
        }
    }

    private fun printCheck(check: Check, products: List<CartDto>) = executor.execute {
        val con = CheckPrinter(context).connect()
        con?.print(check, products)
        // con?.close()
    }

    private fun buildCheck(check: Check) : String {
        return ""
    }
}