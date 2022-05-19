package com.hlypalo.express_kassa.data.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.hlypalo.express_kassa.data.model.*
import java.lang.reflect.Type

class ReceiptElementDeserializer : JsonDeserializer<ReceiptElement> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ReceiptElement? {
        json?.asJsonObject?.get("type")?.asString?.let {
            return context?.deserialize(json, Class.forName(it))
        }
        return null
    }

}