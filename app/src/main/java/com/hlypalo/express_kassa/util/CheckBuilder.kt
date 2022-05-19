package com.hlypalo.express_kassa.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.*
import kotlinx.android.synthetic.main.layout_check.view.*

class CheckBuilder(
    private val context: Context?,
    private val check: Check?,
    scale: Boolean = false
) {

    private val layoutWidth = if (scale) RECEIPT_WIDTH else getPreviewLayoutWidth(context)
    private val scaleRatio: Float = layoutWidth / getPreviewLayoutWidth(context).toFloat()

    companion object {
        private const val RECEIPT_WIDTH = 384
    }

    fun build(template: ReceiptTemplateData?) : Bitmap {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_check, null)

        template?.forEach { row ->
            val tr = createRow(context, view.layout_receipt)
            row.forEach { el ->
                val v = getElementView(el)
                addViewToRow(v, tr)
                if (el is ReceiptTextElement && check != null) {
                    setVariableValue(el)
                }
            }
        }

        view.measure(
            View.MeasureSpec.makeMeasureSpec(layoutWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun getElementView(el: ReceiptElement?) : View? {
        return when(el) {
            is ReceiptTextElement -> {
                getViewForTextElement(el, context).apply {
                    textSize = (26F + (el.size.offset)) * scaleRatio
                }
            }
            is ReceiptListElement -> getViewForListElement(el)
            is ReceiptImageElement -> {
                ImageView(context)
            }
            else -> null
        }
    }

    private fun getViewForListElement(el: ReceiptListElement) : View {
        val layout = TableLayout(context)
        layout.isStretchAllColumns = true
        (check?.products ?: listOf(null)).forEach { p ->

            el.data.forEach { row ->
                val tr = createRow(context, layout)
                row.forEach { el ->
                    if (el is ReceiptTextElement && p != null) {
                        setVariableValue(el, p)
                    }
                    val v = getElementView(el)
                    addViewToRow(v, tr)
                }
            }
        }
        return layout
    }

    private fun setVariableValue(el: ReceiptTextElement, product: CheckProduct? = null) {
        val ind1 = el.text.indexOf('{')
        if (ind1 == -1) {
            return
        }
        val ind2 = el.text.indexOf('}')
        val name = el.text.substring(ind1 + 1, ind2)
        val value = getValue(name, product)
        el.text = el.text.replaceRange(ind1..ind2, value.toString())
    }

    private fun getValue(name: String, product: CheckProduct?) : Any?  {
        CheckVariables.values().find { it.displayName == name }?.let { variable ->
            variable.getVal?.invoke(check!!)?.let {
                return it
            }
        }
        GlobalVariables.values().find { it.displayName == name }?.let { variable ->
            if (variable == GlobalVariables.CUSTOM) {
                // TODO
            }
            variable.getVal?.invoke(check!!)?.let {
                return it
            }
        }
        if (product == null) {
            return null
        }
        ProductVariables.values().find { it.displayName == name }?.let { variable ->
            if (variable == ProductVariables.CUSTOM) {
                // TODO
            }
            variable.getVal?.invoke(product)?.let {
                return it
            }
        }
        return null
    }

}