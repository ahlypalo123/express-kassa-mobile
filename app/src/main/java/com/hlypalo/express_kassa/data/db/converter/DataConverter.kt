package com.hlypalo.express_kassa.data.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hlypalo.express_kassa.data.model.CartProduct

class DataConverter {

    @TypeConverter
    fun fromCartProductList(value: List<CartProduct>): String {
        val gson = Gson()
        val type = object : TypeToken<List<CartProduct>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCartProductList(value: String): List<CartProduct> {
        val gson = Gson()
        val type = object : TypeToken<List<CartProduct>>() {}.type
        return gson.fromJson(value, type)
    }
}