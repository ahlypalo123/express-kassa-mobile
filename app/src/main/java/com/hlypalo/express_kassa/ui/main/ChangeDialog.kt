package com.hlypalo.express_kassa.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.repository.CheckRepository
import kotlinx.android.synthetic.main.dialog_change.*

class ChangeDialog(
    private val delegate: MainView
) : DialogFragment() {

    private val repo: CheckRepository by lazy { CheckRepository() }

    private var cash: Float = 0F

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_change, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val check = repo.getCheck()
        text_total?.text = "К оплате: ${check?.total}"
        input_cash?.addTextChangedListener {
            cash = it.toString().toFloatOrNull() ?: 0F
            val change = cash - check?.total!!
            text_change?.text = "Сдача: $change"
        }
        input_cash?.setText(check?.total.toString())
        btn_continue?.setOnClickListener {
            check?.cash = cash
            repo.updateCheck(check)
            dismiss()
            delegate.openCheckFragment()
        }
    }

}