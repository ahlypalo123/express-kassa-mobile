package com.hlypalo.express_kassa.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.util.inflate
import kotlinx.android.synthetic.main.dialog_variable.*

class VariableDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_variable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_variables_common?.layoutManager = GridLayoutManager(context, 2)
        list_variables_common?.adapter = Adapter(arrayOf(
            "Общая стоимость",
            "Способ оплаты",
            "Общее количество",
            "Дата",
            "Время",
            "Имя работника",
            "Номер чека",
        ))
        list_variables_product?.layoutManager = GridLayoutManager(context, 2)
        list_variables_product?.adapter = Adapter(arrayOf(
            "Название",
            "Количество",
            "Цена",
            "Стоимость",
            "Пользовательская переменная",
        ))
        list_variables_settings?.layoutManager = GridLayoutManager(context, 2)
        list_variables_settings?.adapter = Adapter(arrayOf(
            "Наименование предприятия",
            "ИНН",
            "СНО",
            "Адрес 1",
            "Адрес 2",
            "Email",
            "Web сайт",
            "Пользовательская переменная",
        ))
    }

    inner class Adapter(var items: Array<String>) :
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
                    (parentFragment as? TextFormatDialog)?.addVariable(text)
                    dismiss()
                }
            }

        }
    }

}