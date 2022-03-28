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

class CheckFragment : Fragment() {

    private val repo: CheckRepository by lazy { CheckRepository() }
    private val productRepo: ProductRepository by lazy { ProductRepository() }
    private var total: Float = 0.0f

    companion object {
        private val applicationUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

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

    private fun printCheck(check: Check, products: List<CartDto>) {
        val address = App.sharedPrefs.getString(PREF_PRINTER_ADDRESS, "")
        if (address.isNullOrBlank()) {
            return
        }
        val bluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter
        val device = adapter.getRemoteDevice(address)
        Thread {
            val socket = device.createRfcommSocketToServiceRecord(applicationUuid)
            adapter.cancelDiscovery()
            socket.connect()
            Log.d(TAG, "device $address successfully connected")
            val os = socket.outputStream
            val bill = generateBill(check, products)

            os.write(bill.toByteArray())
            val gs = 29
            os.write(intToByteArray(gs).toInt())
            val h = 104
            os.write(intToByteArray(h).toInt())
            val n = 162
            os.write(intToByteArray(n).toInt())

            val gs_width = 29
            os.write(intToByteArray(gs_width).toInt())
            val w = 119
            os.write(intToByteArray(w).toInt())
            val n_width = 2
            os.write(intToByteArray(n_width).toInt())

            socket.close()

        }.start()
    }

    private fun generateBill(check: Check, products: List<CartDto>) : String {
        val date = DateTime(check.date)
        val sb = StringBuilder()
        sb.append("                ${date.toString("yyyy")} ${date.toString("MM")}   ")
        sb.append("\n")
        sb.append("--------------------------------")
        sb.append("\n")
        sb.append(String.format("%1$10s %2$4s %3$4s %4$4s", "Item", "Qty", "Rate", "Total"))
        sb.append("\n")
        sb.append("-------------------------------")
        sb.append("\n")
        for (p in products) {
            sb.append(String.format("%1$10s %2$4s %3$4s %4$4s", p.name, p.count, p.price, p.price * p.count))
            sb.append("\n")
        }
        sb.append("-------------------------------")
        sb.append("\n")
        sb.append("    Total Value:       ${check.total}")
        sb.append("\n")
        sb.append("\n")
        sb.append("\n")
        return sb.toString()
    }

    private fun intToByteArray(value: Int): Byte {
        val b = ByteBuffer.allocate(4).putInt(value).array()
        for (k in b.indices) {
            println(
                "Selva  [" + k + "] = " + "0x"
                        + UnicodeFormatter.byteToHex(b[k])
            )
        }
        return b[3]
    }

    private fun buildCheck(check: Check) : String {
        return ""
    }
}