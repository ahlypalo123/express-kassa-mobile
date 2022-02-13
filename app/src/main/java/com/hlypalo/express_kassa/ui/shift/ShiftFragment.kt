package com.hlypalo.express_kassa.ui.shift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.ShiftDetails
import com.hlypalo.express_kassa.data.model.ShiftRequest
import com.hlypalo.express_kassa.util.enqueue
import kotlinx.android.synthetic.main.fragment_shift.*

class ShiftFragment : Fragment() {

    private val api: ApiService by lazy { ApiService.getInstance() }
    private var shift: ShiftDetails? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shift, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        api.getCurrentShift().enqueue {
            onResponse = {
                it?.let {
                    shift = it
                }
            }
            onError = {
                shift = null
            }
            finally = {
                updateUi()
            }
        }
    }

    private fun validateEmployeeName() : Boolean {
        if (layout_employee_name?.isEmpty() == true) {
            layout_employee_name?.error = "Имя работника не может быть пустым"
            return false
        }
        layout_employee_name?.error = null
        return true
    }

    private fun updateUi() {
        input_employee_name?.isEnabled = shift == null

        if (shift == null) {
            btn_manage_shift?.text = "Открыть смену"
            btn_manage_shift?.setOnClickListener {
                if (validateEmployeeName()) {
                    api.openShift(
                        ShiftRequest(input_employee_name?.text?.toString()!!)
                    ).enqueue {
                        onResponse = {
                            shift = it
                            updateUi()
                        }
                    }
                }
            }
        } else {
            input_employee_name?.setText(shift?.employeeName)
            btn_manage_shift?.text = "Закрыть смену"
            btn_manage_shift?.setOnClickListener {
                api.closeShift().enqueue {
                    onResponse = {
                        shift = null
                        updateUi()
                    }
                }
            }
        }
    }

}