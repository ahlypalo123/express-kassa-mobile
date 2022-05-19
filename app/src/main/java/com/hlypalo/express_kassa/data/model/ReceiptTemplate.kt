package com.hlypalo.express_kassa.data.model

import android.graphics.Bitmap
import android.widget.TextView
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.lang.reflect.Type

@JsonClass(generateAdapter = true)
interface ReceiptElement {
    val type: String
}

@JsonClass(generateAdapter = true)
enum class ReceiptElementGroupType {
    LIST, GROUP
}

typealias ReceiptTemplateData = MutableList<MutableList<ReceiptElement>>

@JsonClass(generateAdapter = true)
data class ReceiptTextElement(
    var text: String,
    var alignment: Int = TextView.TEXT_ALIGNMENT_CENTER,
    var style: MutableList<ReceiptTextStyle> = mutableListOf(),
    var size: ReceiptTextSize = ReceiptTextSize.NORMAL,
    override val type: String = ReceiptTextElement::class.java.name
) : ReceiptElement, Serializable

@JsonClass(generateAdapter = true)
data class ReceiptListElement(
    var data: ReceiptTemplateData,
    var groupType: ReceiptElementGroupType,
    override val type: String = ReceiptListElement::class.java.name
) : ReceiptElement, Serializable

@JsonClass(generateAdapter = true)
data class ReceiptImageElement(
    var bmp: Bitmap,
    var url: String,
    var height: Int,
    var width: Int,
    override val type: String = ReceiptImageElement::class.java.name
) : ReceiptElement, Serializable

@JsonClass(generateAdapter = true)
enum class ReceiptTextSize(val offset: Int, val nameRes: String) {
    SMALLER(-8, "Мельче"), SMALL(-4, "Мелкий"), NORMAL(0, "Обычный"),
    BIG(4, "Большой"), LARGE(8, "Больше"), LARGER(16, "Огромный")
}

@JsonClass(generateAdapter = true)
enum class ReceiptTextStyle {
    BOLD, UNDERLINED, ITALIC
}

enum class CheckVariables(val displayName: String, val getVal: ((Check) -> Any?)?) {
    COUNT("Общее количество товаров", {
        it.total
    }),
    TOTAL("Общая стоимость", {
        it.total
    }),
    PAYMENT_METHOD("Способ оплаты", {
        if (it.paymentMethod == PaymentMethod.CASH) "Наличные" else "Карта"
    }),
    DATE("Дата", {
        org.joda.time.DateTime(it.date).toString("yyyy-MM-dd")
    }),
    TIME("Время", {
        org.joda.time.DateTime(it.date).toString("hh:ss")
    }),
    EMPLOYEE_NAME("Имя работника", {
        it.employeeName
    }),
    ID("Номер чека", {
        it.id
    })
}

enum class ProductVariables(val displayName: String, val getVal: ((CheckProduct) -> Any?)?) {
    PRODUCT_NAME("Название", {
        it.name
    }),
    COUNT("Количество", {
        it.count
    }),
    PRICE("Цена", {
        it.price
    }),
    TOTAL("Стоимость", {
        it.price * it.count
    }),
    CUSTOM("Переменная товара", null)
}

enum class GlobalVariables(val displayName: String, val getVal: ((Check) -> Any?)?) {
    NAME("Наименование предприятия", {
        it.name
    }),
    TAX_TYPE("Тип налогообложения", {
        it.taxType
    }),
    ADDRESS1("Адрес 1", {
        it.address
    }),
    ADDRESS2("Адрес 2", {
        it.address
    }),
    EMAIL("Email", {
        it.address
    }),
    WEB_SITE("Веб-сайт", {
        it.address
    }),
    CUSTOM("Глобальная переменная", null)
}
