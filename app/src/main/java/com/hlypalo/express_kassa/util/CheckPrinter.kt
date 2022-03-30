package com.hlypalo.express_kassa.util

import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import android.view.Gravity
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.Check
import org.joda.time.DateTime
import java.io.IOException
import java.io.OutputStream
import java.util.*

class CheckPrinter(private val context: Context?) {

    companion object {
        private val applicationUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private var tries = 3

    fun connect() : Connection? {
        val address = App.sharedPrefs.getString(PREF_PRINTER_ADDRESS, "")
        if (address.isNullOrBlank()) {
            return null
        }
        val bluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter
        val device = adapter.getRemoteDevice(address)
        val socket = device.createRfcommSocketToServiceRecord(applicationUuid)
        adapter.cancelDiscovery()
        try {
            socket.connect()
        } catch (e: IOException) {
            return if (tries > 0) {
                tries--
                Log.d(TAG, "Connection failed. Trying one more time. Tries left: $tries")
                connect()
            } else {
                Log.d(TAG, "Cannot connect to the device $address, giving up")
                null
            }
        }
        Log.d(TAG, "device $address successfully connected")
        return Connection(socket.outputStream)
    }

    inner class Connection(private val os: OutputStream) {
        fun print(check: Check, products: List<CartDto>) : String {
            val date = DateTime.now()
            val sb = StringBuilder()

            val pw = CheckPrinterWriter(context, os)

            pw.writeLine("ДОБРО ПОЖАЛОВАТЬ!", Gravity.CENTER)
            pw.writeLine("Кассовый чек", Gravity.CENTER)
            pw.writeLineBreak()
            for (p in products) {
                pw.writeLine(p.name, "${p.count} x ${p.price}")
                pw.writeLine("= ${p.price * p.count}", Gravity.END)
            }
            pw.writeLine("___________________________", Gravity.CENTER)
            pw.writeLineBold("ИТОГО", check.total.toString())
            pw.writeLine("___________________________", Gravity.CENTER)
            pw.writeLine("СУММА С НДС 0%", check.total.toString())
            pw.writeLine("ДАТА", date.toString("dd.MM.yyyy"))
            pw.writeLineBreak()
            pw.writeLineBreak()
            pw.writeLineBreak()
            return sb.toString()
        }

        public fun close() {
            os.close()
        }
    }
}