package com.hlypalo.express_kassa.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CheckVariables
import com.hlypalo.express_kassa.data.model.GlobalVariables
import com.hlypalo.express_kassa.data.model.ProductVariables
import com.hlypalo.express_kassa.util.CheckBuilder
import com.hlypalo.express_kassa.util.inflate
import kotlinx.android.synthetic.main.dialog_variable.*

interface VariableDelegate {
    fun onVariableSelected(variable: String)
}

class VariableDialog(
    private val delegate: VariableDelegate?
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_variable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TooltipCompat.setTooltipText(text_common, "Переменные, относящиеся к продаже")
        TooltipCompat.setTooltipText(text_product, "Значения переменных указываются при создании продукта")
        TooltipCompat.setTooltipText(text_customizable, "Значение переменной можно предопределить в настройках приложения")
        listOf<View>(text_common, text_product, text_customizable).forEach {
            it.setOnClickListener { v ->
                v.performLongClick()
            }
        }

        list_variables_common?.layoutManager = GridLayoutManager(context, 2)
        list_variables_common?.adapter = Adapter(CheckVariables.values().map { it.displayName })
        list_variables_product?.layoutManager = GridLayoutManager(context, 2)
        list_variables_product?.adapter = Adapter(ProductVariables.values().map { it.displayName })
        list_variables_customizable?.layoutManager = GridLayoutManager(context, 2)
        list_variables_customizable?.adapter = Adapter(GlobalVariables.values().map { it.displayName })
    }

    inner class Adapter(var items: List<String>) :
        androidx.recyclerview.widget.RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.item_variable))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(items[position])

        override fun getItemCount() = items.size

        inner class ViewHolder(itemView: View) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            fun bind(text: String) {
                (itemView as? AppCompatButton)?.text = text
                itemView.setOnClickListener {
                    delegate?.onVariableSelected(text)
                    dismiss()
                }
            }

        }
    }

}