package com.hlypalo.express_kassa.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.ReceiptTextElement
import com.hlypalo.express_kassa.data.model.ReceiptTextSize
import com.hlypalo.express_kassa.data.model.ReceiptTextStyle
import com.hlypalo.express_kassa.util.CustomSpinner
import kotlinx.android.synthetic.main.dialog_text_format.*

class TextFormatDialog(
    private val el: ReceiptTextElement
) : BottomSheetDialogFragment(), VariableDelegate {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        return inflater.inflate(R.layout.dialog_text_format, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            dialog?.dismiss()
        }
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spinner_text_size?.adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            ReceiptTextSize.values().map { it.nameRes }
        )

        input_text?.setText(el.text)
        val pos = when (el.alignment) {
            TextView.TEXT_ALIGNMENT_TEXT_START -> 0
            TextView.TEXT_ALIGNMENT_TEXT_END -> 2
            else -> 1
        }
        toggle_alignment?.checkByPosition(pos)
        check_bold?.isChecked = el.style.contains(ReceiptTextStyle.BOLD) == true
        check_underlined?.isChecked = el.style.contains(ReceiptTextStyle.UNDERLINED) == true

        input_text?.addTextChangedListener {
            el.text = input_text?.text.toString()
            updateUi()
        }

        toggle_alignment?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val position = view.findViewById<View?>(checkedId)?.tag.toString().toInt()
            el.alignment = when (position) {
                0 -> TextView.TEXT_ALIGNMENT_TEXT_START
                2 -> TextView.TEXT_ALIGNMENT_TEXT_END
                else -> TextView.TEXT_ALIGNMENT_CENTER
            }

            updateUi()
        }
        check_bold?.setOnCheckedChangeListener { _, b ->
            if (b) {
                el.style.add(ReceiptTextStyle.BOLD)
            } else {
                el.style.remove(ReceiptTextStyle.BOLD)
            }
            updateUi()
        }
        check_underlined?.setOnCheckedChangeListener { _, b ->
            if (b) {
                el.style.add(ReceiptTextStyle.UNDERLINED)
            } else {
                el.style.remove(ReceiptTextStyle.UNDERLINED)
            }
            updateUi()
        }

        spinner_text_size?.setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
            override fun onSpinnerOpened(spin: Spinner?) {
            }

            override fun onSpinnerClosed(spin: Spinner?) {
                el.size = ReceiptTextSize.values()[spinner_text_size?.selectedItemPosition!!]
                updateUi()
            }

        })

        btn_ok?.setOnClickListener {
            dismiss()
        }

        btn_delete?.setOnClickListener {
            (parentFragment as? EditTemplateFragment)?.removeUpdatedElement()
            dismiss()
        }

        btn_add_variable?.setOnClickListener {
            VariableDialog(this).show(childFragmentManager, null)
        }
    }

    override fun onVariableSelected(variable: String) {
        input_text?.setText("${input_text?.text?.toString()} {$variable}")
    }

    private fun updateUi() {
        (parentFragment as? EditTemplateFragment)?.updateUi()
    }

    private fun MaterialButtonToggleGroup.checkByPosition(position: Int) {
        (children.toList()[position] as? Button)?.id?.let { check(it) }
    }

}