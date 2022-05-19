package com.hlypalo.express_kassa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Product(
    var id: Long,
    val name: String,
    val price: Float,
    var photoUrl: String?,
    val barCode: String?,
    val data: Map<String, String>? = null
) : Serializable

data class CheckProduct(
    val id: Long? = null,
    val name: String? = null,
    var count: Int,
    val productId: Long? = null,
    val price: Float
)

data class ShiftDetails(
    val id: Long,
    val employeeName: String,
    val startDate: Long,
    val endDate: Long
)

@JsonClass(generateAdapter = true)
data class ReceiptTemplate (
    val id: Long = 0,
    var active: Boolean = false,
    var data: ReceiptTemplateData = mutableListOf()
) : Serializable

@Entity
@JsonClass(generateAdapter = true)
data class Check(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    var total: Float? = null,
    var discount: Float? = null,
    var paymentMethod: PaymentMethod? = null,
    var completed: Boolean = false,
    var cash: Float? = null,
    var customerName: String? = null,
    var customerLast4: Int? = null,
    var products: MutableList<CheckProduct> = mutableListOf(),
    var date: Long? = null,
    var inn: String? = null,
    var employeeName: String? = null,
    var address: String? = null,
    var name: String? = null,
    var taxType: String? = null,
    val data: Map<String, String>? = null,
    val merchantData: Map<String, String>? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class MerchantDetails(
    val id: Long,
    val inn: String?,
    val address: String?,
    val name: String?,
    val taxType: String?,
    var shift: ShiftDetails? = null,
    val data: Map<String, String>? = null
)