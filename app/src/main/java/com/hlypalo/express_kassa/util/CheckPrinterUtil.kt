package com.hlypalo.express_kassa.util

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.model.MerchantDetails
import java.util.*
import java.util.concurrent.Executors

class CheckPrinterUtil {

    companion object {
        private val executor = Executors.newSingleThreadExecutor();

        fun printCheck(
            check: Check,
            products: List<CartDto>,
            view: View?,
            context: Context?
        ) = executor.execute {
            startPrinting(check, products, view, context)
        }

        private fun startPrinting(
            check: Check,
            products: List<CartDto>,
            view: View?,
            context: Context?
        ) {
            val address = App.sharedPrefs.getString(PREF_PRINTER_ADDRESS, "")
            if (address.isNullOrBlank()) {
                return
            }
            val printer = CheckPrinter(context, address)
            try {
                printer.init()
            } catch (ex: Exception) {
                ex.printStackTrace()
                return
            }

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
            if (con == null) {
                view?.post {
                    context?.confirm("Не удалось подключиться к принтеру. Проверьте Bluetooth соединение, либо попробуйте еще раз", onYes = {
                        startPrinting(check, products, view, context)
                    }, yesText = "Попробовать еще")
                }
            }
            timer.cancel()
            con?.print(check, products)
        }
    }

}