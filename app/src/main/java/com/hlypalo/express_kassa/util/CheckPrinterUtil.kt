package com.hlypalo.express_kassa.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.data.model.Check
import java.util.*

object CheckPrinterUtil {

    fun printCheck(
        check: Check,
        view: View?,
        context: Context?
    ) {
        val address = App.sharedPrefs.getString(PREF_PRINTER_ADDRESS, "")
        if (address.isNullOrBlank()) {
            return
        }
        val printer = CheckPrinter(context, address)
        printer.init()

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                view?.post {
                    Toast.makeText(
                        context,
                        "Пожалуйста подождите, первый раз подключение может занять много времени",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }, 2000L)
        val con = printer.connect()
        timer.cancel()
        con?.print(check)
    }

}