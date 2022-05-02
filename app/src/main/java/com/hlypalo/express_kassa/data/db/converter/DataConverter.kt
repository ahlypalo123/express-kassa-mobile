package com.hlypalo.express_kassa.data.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hlypalo.express_kassa.data.model.CheckProduct

class DataConverter {

    @TypeConverter
    fun fromCartProductList(value: List<CheckProduct>): String {
        val gson = Gson()
        val type = object : TypeToken<List<CheckProduct>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCartProductList(value: String): List<CheckProduct> {
        val gson = Gson()
        val type = object : TypeToken<List<CheckProduct>>() {}.type
        return gson.fromJson(value, type)
    }
}