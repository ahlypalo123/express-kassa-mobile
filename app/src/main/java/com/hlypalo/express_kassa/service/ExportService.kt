package com.hlypalo.express_kassa.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.model.OrderColumn
import com.hlypalo.express_kassa.data.model.PaymentMethod
import com.hlypalo.express_kassa.data.repository.CheckRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime

class ExportService : Service() {

    private val repo: CheckRepository by lazy { CheckRepository() }

    companion object {
        private const val NOTIFICATION_ID = 100
        private const val DEFAULT_CHANNEL_ID = "DEFAULT_CHANNEL"

        @RequiresApi(Build.VERSION_CODES.O)
        fun start(activity: Activity?, uri: Uri?) {
            val intent = Intent(activity, ExportService::class.java).apply {
                data = uri
            }
            activity?.startForegroundService(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DEFAULT_CHANNEL_ID,
                "ExpressKassa",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, DEFAULT_CHANNEL_ID)
            .setContentTitle("Экспорт данных")
            .setContentText("Подождите, данные экспортируются")
            .setProgress(0, 0, true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            // .setTicker("Ticker text")
            .build()
        startForeground(NOTIFICATION_ID, notification)

        repo.getCheckHistory(OrderColumn.DATE, callback = {
            intent?.data?.let { uri ->
                exportToFile(it ?: listOf(), uri)
            }
        }, error = {
            stopSelf()
        })

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.i("TAG", "Service destroyed")
        super.onDestroy()
    }

    private fun exportToFile(items: List<Check>, uri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Кассовый чек")
        val style = workbook.createCellStyle().apply {
            borderLeft = BorderStyle.MEDIUM
            borderRight = BorderStyle.MEDIUM
            borderTop = BorderStyle.MEDIUM
            borderBottom = BorderStyle.MEDIUM
            alignment = HorizontalAlignment.CENTER
        }

        val set = mutableSetOf<String?>()
        items.forEach {
            it.products.forEach { p ->
                set.add(p.name)
            }
        }

        val headers = mutableListOf(
            "Дата",
            "Имя работника",
            "Скидка",
            "Способ оплаты",
            "Общая стоимость",
            null
        )
        headers.addAll(set)

        sheet.createRow(1, headers, style)

        items.forEachIndexed { i, el ->
            val data = mutableListOf<Any?>(
                DateTime(el.date ?: 0).toString("yyyy.MM.dd hh:mm"),
                el.employeeName,
                (el.discount ?: 0).toDouble(),
                if (el.paymentMethod == PaymentMethod.CASH) "Наличные" else "Карта",
                el.total?.toDouble() ?: 0.0,
                null
            )
            set.forEach { name ->
                data.add(el.products.find { name == it.name }?.count ?: "-")
            }
            sheet.createRow(i + 2, data, style)
        }

        applicationContext?.contentResolver?.openOutputStream(uri)?.use {
            workbook.write(it)
        }

        stopSelf()
    }

    private fun XSSFSheet.createRow(i: Int, data: List<Any?>, style: XSSFCellStyle) {
        val row = createRow(i + 1)
        data.forEachIndexed {  ind, v ->
            val cel = row.createCell(ind)
            if (v == null) {
                cel.setCellValue("")
                return@forEachIndexed
            }
            cel.cellStyle = style
            when (v) {
                is String -> {
                    cel.setCellValue(v)
                }
                is Double -> {
                    cel.setCellValue(v)
                }
                is Int -> {
                    cel.setCellValue(v.toDouble())
                }
            }
            // setColumnWidth(i, 125)
            // setColumnBreak(i)
        }
    }
}