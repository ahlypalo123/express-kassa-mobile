package com.hlypalo.express_kassa.ui.settings

import android.widget.TextView
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ReceiptTemplate(
    val lines: MutableList<MutableList<Element>>
) : Serializable {

    @JsonClass(generateAdapter = true)
    data class Element(
        var text: String,
        var alignment: Int = TextView.TEXT_ALIGNMENT_CENTER,
        var style: MutableList<TextStyle> = mutableListOf(),
        var size: TextSize = TextSize.NORMAL
    ) : Serializable

    enum class TextSize(val offset: Int, val nameRes: String) {
        SMALL(-4, "Мелкий"), NORMAL(0, "Обычный"),
        BIG(4, "Большой"), LARGE(8, "Огромный")
    }

    enum class TextStyle {
        BOLD, UNDERLINED, ITALIC
    }

}
