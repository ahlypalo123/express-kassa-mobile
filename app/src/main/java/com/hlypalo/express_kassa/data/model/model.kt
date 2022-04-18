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

@Entity
data class CartProduct(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
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
    @PrimaryKey(autoGenerate = true) val id: Long,
    val total: Float,
    val discount: Float?,
    val paymentMethod: PaymentMethod,
    val customerName: String?,
    val customerLast4: Int?,
    val products: List<CartProduct>,
    val date: Long,

    val inn: String? = null,
    val employeeName: String? = null,
    val address: String? = null,
    val name: String? = null,
    val taxType: String? = null,
) : Serializable

@JsonClass(generateAdapter = true)
data class MerchantDetails(
    val id: Long,
    val inn: String?,
    val address: String?,
    val name: String?,
    val taxType: String?,
)