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
import com.hlypalo.express_kassa.ui.base.NavigationFragment
import com.hlypalo.express_kassa.util.*
import kotlinx.android.synthetic.main.fragment_printers.*
import java.nio.ByteBuffer
import java.util.*

class PrintersFragment : Fragment() {

    companion object {
        private val applicationUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

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
            val bill = CheckBuilder.build()

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
            for (d in devices) {
                listAdapter.add("${d.name}\n${d.address}")
            }
        } else {
            listAdapter.add("None Paired")
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
            text_connected?.text = "No devices connected"
        } else {
            text_connected?.text = "Last connected device: $last"
        }
    }

}