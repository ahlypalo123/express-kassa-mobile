package com.hlypalo.express_kassa.ui.devices

import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.model.PaymentMethod
import com.hlypalo.express_kassa.ui.base.NavigationFragment
import com.hlypalo.express_kassa.util.*
import kotlinx.android.synthetic.main.fragment_printers.*
import java.util.*

class PrintersFragment : Fragment() {

    private lateinit var listAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_printers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

        updateStatus()
        updateList()

        btn_test?.setOnClickListener {
            printTestCheck()
        }
    }

    private fun printTestCheck() {
        val check = Check(
            id = 0,
            total = 320f,
            discount = null,
            paymentMethod = PaymentMethod.CASH,
            customerName = null,
            customerLast4 = null,
            products = listOf(),
            date = System.currentTimeMillis(),
            employeeName = "Andrey"
        )

        val products = listOf(
            CartDto(
                count = 1,
                name = "Капучино",
                price = 100f
            ),
            CartDto(
                count = 1,
                name = "Американо",
                price = 70f
            ),
            CartDto(
                count = 1,
                name = "Чизкейк",
                price = 150f
            )
        )

        val con = CheckPrinter(context).connect()
        con?.print(check, products)
        // con?.close()
    }

    private fun updateList() {
        listAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        paired_devices.adapter = listAdapter
        paired_devices.setOnItemClickListener { _, tv, _, _ ->
            try {
                val mDeviceInfo = (tv as TextView).text.toString()
                val address = mDeviceInfo.substring(mDeviceInfo.length - 17)
                val name = mDeviceInfo.substring(0, mDeviceInfo.length - 17)
                App.prefEditor.putString(PREF_PRINTER_ADDRESS, address).commit()
                App.prefEditor.putString(PREF_PRINTER_NAME, name).commit()
                updateStatus()
                Toast.makeText(context, "Connected Successfully", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Log.e(TAG, ex.localizedMessage, ex)
            }
        }

        val bluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val devices = bluetoothAdapter.bondedDevices

        if (devices.size > 0) {
            text_empty?.visibility = View.GONE
            for (d in devices) {
                listAdapter.add("${d.name}\n${d.address}")
            }
        } else {
            text_empty?.visibility = View.VISIBLE
        }
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

    private fun updateStatus() {
        val last = App.sharedPrefs.getString(PREF_PRINTER_NAME, "")
        if (last.isNullOrBlank()) {
            text_connected?.text = "Выберите устройство из списка для подключения"
        } else {
            text_connected?.text = "Подключенное устройство: $last"
        }
    }

}