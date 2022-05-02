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
    val barCode: String?
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

@Entity
@JsonClass(generateAdapter = true)
data class Check(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    var total: Float? = null,
    var discount: Float? = null,
    var paymentMethod: PaymentMethod? = null,
    var cash: Float? = null,
    var change: Float? = null,
    var customerName: String? = null,
    var customerLast4: Int? = null,
    var products: MutableList<CheckProduct> = mutableListOf(),
    var date: Long? = null,
    var inn: String? = null,
    var employeeName: String? = null,
    var address: String? = null,
    var name: String? = null,
    var taxType: String? = null,
) : Serializable

@JsonClass(generateAdapter = true)
data class MerchantDetails(
    val id: Long,
    val inn: String?,
    val address: String?,
    val name: String?,
    val taxType: String?,
    val shift: ShiftDetails? = null
)