package com.hlypalo.express_kassa.ui.check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.repository.CheckRepository
import kotlinx.android.synthetic.main.dialog_discount.*

class DiscountDialog(
    private val delegate: CheckPreviewFragment
) : DialogFragment() {

    private var disableListeners = false
    private val repo: CheckRepository by lazy { CheckRepository() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_discount, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val check = repo.getCheck()

        input_discount_value?.addTextChangedListener {
            if (disableListeners) {
                return@addTextChangedListener
            }
            if (it.isNullOrBlank()) {
                return@addTextChangedListener
            }
            val percent = ((it.toString().toFloat() / (check?.total ?: 0F)) * 100).toInt()
            disableListeners = true
            input_discount_percent?.setText(percent.toString())
            disableListeners = false
        }
        input_discount_value?.setText((check?.discount ?: 0).toString())

        input_discount_percent?.addTextChangedListener {
            if (disableListeners) {
                return@addTextChangedListener
            }
            if (it.isNullOrBlank()) {
                return@addTextChangedListener
            }
            val value = (it.toString().toFloat() * ((check?.total ?: 0F) / 100)).toInt()
            disableListeners = true
            input_discount_value?.setText(value.toString())
            disableListeners = false
        }

        btn_add?.setOnClickListener {
            val lastDiscount = check?.discount ?: 0F
            val discount = input_discount_value?.text.toString().toFloatOrNull() ?: 0F
            check?.discount = discount
            check?.total = check?.total!! - discount + lastDiscount
            if (check.cash != null) {
                check.cash = check.cash!! - discount + lastDiscount
            }
            repo.updateCheck(check)
            delegate.updateUi()
            dialog?.dismiss()
        }

        btn_cancel?.setOnClickListener {
            dialog?.dismiss()
        }
    }

}