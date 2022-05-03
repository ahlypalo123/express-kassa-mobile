package com.hlypalo.express_kassa.util

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.hlypalo.express_kassa.data.model.Check
import android.graphics.Canvas
import android.view.ContextMenu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CheckProduct
import com.hlypalo.express_kassa.data.model.PaymentMethod
import kotlinx.android.synthetic.main.item_check_product.view.*
import kotlinx.android.synthetic.main.layout_check.view.*
import org.joda.time.DateTime

object CheckBuilder {

    fun build(check: Check, context: Context?) : Bitmap {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_check, null)
        with(view) {
//            if (withPadding) {
//                layout_check?.setPadding(32, 0, 32, 32)
//            }

            text_check_id?.text = check.id.toString()
            text_total.text = check.total.toString()

            setTextIfNotNull(check.name, text_merchant_name)
            setTextIfNotNull(check.address, text_address)
            setTextIfNotNull(check.taxType, text_tax_type, text_tax_type_header)
            setTextIfNotNull(check.inn, text_inn, text_inn_header)
            setTextIfNotNull(check.employeeName, text_employee_name, text_employee_header)
            setTextIfNotNull((check.discount ?: "").toString(), text_discount, text_discount_header)

            if (check.cash != 0F && check.cash != null && check.cash != check.total) {
                text_change?.visibility = View.VISIBLE
                text_change_header?.visibility = View.VISIBLE
                text_change?.text = (check.cash!! - check.total!!).toString()
            } else {
                text_change?.visibility = View.GONE
                text_change_header?.visibility = View.GONE
            }

            list_check_products?.layoutManager = LinearLayoutManager(context)
            list_check_products?.adapter = Adapter(check.products)
            val pm = if (check.paymentMethod == PaymentMethod.CASH) "ОПЛАТА НАЛИЧНЫМИ" else "ОПЛАТА КАРТОЙ"
            text_payment_type?.text = pm
            val paid = if (check.paymentMethod == PaymentMethod.CASH) {
                check.cash
            } else {
                check.total
            }
            text_paid?.text = paid.toString()
            val date = DateTime(check.date)
            text_date?.text = "ДАТА ${date.toString("yyyy-MM-dd")}"
            text_time?.text = "ВРЕМЯ ${date.toString("hh:mm")}"
        }

        view.measure(
            View.MeasureSpec.makeMeasureSpec(384, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight);

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun setTextIfNotNull(text: String?, view: TextView?, header: TextView? = null) {
        view?.visibility = if (text.isNullOrBlank()) View.GONE else View.VISIBLE
        header?.visibility = if (text.isNullOrBlank()) View.GONE else View.VISIBLE
        view?.text = text
    }

    class Adapter(private val items: List<CheckProduct>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflate(R.layout.item_check_product))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: CheckProduct) = with(itemView) {
                text_check_name?.text = item.name
                text_check_price?.text = "${item.count} x ${item.price}"
                text_check_total?.text = "= ${item.count * item.price}"
            }
        }
    }

}