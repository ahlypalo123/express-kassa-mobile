package com.hlypalo.express_kassa.data.api

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.hlypalo.express_kassa.data.model.*
import com.squareup.moshi.asArrayType
import java.lang.reflect.Type

class ReceiptElementSerializer : JsonSerializer<ReceiptElement> {
    override fun serialize(
        src: ReceiptElement?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        src ?: return null
        return Gson().toJsonTree(src, Class.forName(src.type))
    }
}