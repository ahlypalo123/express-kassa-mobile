package com.hlypalo.express_kassa.ui.settings

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.*
import com.hlypalo.express_kassa.ui.main.NavigationFragment
import com.hlypalo.express_kassa.util.*
import kotlinx.android.synthetic.main.fragment_printers.*
import java.util.*
import java.util.concurrent.Executors

class PrintersFragment(
    private var forResult: Boolean = false
) : Fragment() {

    private lateinit var listAdapter: ArrayAdapter<String>
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val executor = Executors.newSingleThreadExecutor()

    private val requestEnableBluetooth = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            updateList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("forResult", forResult)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        savedInstanceState?.getBoolean("forResult")?.let {
            forResult = it
        }
        return inflater.inflate(R.layout.fragment_printers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        if (!forResult) {
            activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
            activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)

        val bluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

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

        if (bluetoothAdapter.isEnabled) {
            updateList()
        } else {
            requestEnableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        updateStatus()

        btn_test?.setOnClickListener {
            print()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_devices, menu)
    }

    private fun print() {
        if (forResult) {
            activity?.supportFragmentManager?.popBackStack()
            return
        }
        executor.execute {
            try {
                BluetoothPrinterUtil.printCheck(getTestCheck(), view, context)
            } catch (ex: Exception) {
                ex.printStackTrace()
                view?.post {
                    activity?.confirm("Не удалось установить соединение с принтером")
                }
            }
        }
    }

    private fun getTestCheck() : Check {
        val check = Check(
            id = 54,
            paymentMethod = PaymentMethod.CARD,
            date = System.currentTimeMillis(),
            employeeName = "Андрей",
            taxType = "ПСН",
            inn = "500100732259",
            address = "75, ул. Чехова, г. Таганрог",
            name = "ГБПОУ РО \"ТАВИАК\""
        )

        val products = mutableListOf<CheckProduct>()

        val list = resources.openRawResource(R.raw.products).reader().readLines()
        val random = Random()

        for (i in 0 .. 10) {
            val product = CheckProduct(
                name = list[random.nextInt(list.size - 1)],
                price = (random.nextInt(100) * 10).toFloat(),
                count = random.nextInt(4) + 1
            )
            products.add(product)
        }

        check.total = products.map { it.price }.sum()
        check.products.addAll(products)

        return check
    }

    private fun updateList() {
        val devices = bluetoothAdapter.bondedDevices

        listAdapter.clear()

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
            R.id.refresh -> {
                updateList()
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