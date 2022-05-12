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
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.util.CustomSpinner
import kotlinx.android.synthetic.main.dialog_text_format.*

class TextFormatDialog(
    private val el: ReceiptElement
) : BottomSheetDialogFragment() {

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

    fun addVariable(variable: String) {
        input_text?.setText("${input_text?.text?.toString()} {$variable}")
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
        radio_alignment?.checkByPosition(pos)
        check_bold?.isChecked = el.style.contains(ReceiptStyle.BOLD) == true
        check_underlined?.isChecked = el.style.contains(ReceiptStyle.UNDERLINED) == true

        input_text?.addTextChangedListener {
            el.text = input_text?.text.toString()
            updateUi()
        }

        radio_alignment?.setOnCheckedChangeListener { _, i ->
            el.alignment = when (radio_alignment?.getCheckedPosition()!!) {
                0 -> TextView.TEXT_ALIGNMENT_TEXT_START
                2 -> TextView.TEXT_ALIGNMENT_TEXT_END
                else -> TextView.TEXT_ALIGNMENT_CENTER
            }

            updateUi()
        }
        check_bold?.setOnCheckedChangeListener { _, b ->
            if (b) {
                el.style.add(ReceiptStyle.BOLD)
            } else {
                el.style.remove(ReceiptStyle.BOLD)
            }
            updateUi()
        }
        check_underlined?.setOnCheckedChangeListener { _, b ->
            if (b) {
                el.style.add(ReceiptStyle.UNDERLINED)
            } else {
                el.style.remove(ReceiptStyle.UNDERLINED)
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
            (parentFragment as? ReceiptLayoutFragment)?.removeUpdatedElement()
            dismiss()
        }

        btn_add_variable?.setOnClickListener {
            VariableDialog().show(childFragmentManager, null)
        }
    }

    private fun updateUi() {
        (parentFragment as? ReceiptLayoutFragment)?.updateUi()
    }

    private fun RadioGroup.checkByPosition(position: Int) {
        (children.toList()[position] as? RadioButton)?.isChecked = true
    }

    private fun RadioGroup.getCheckedPosition() : Int =
        view?.findViewById<View>(checkedRadioButtonId)?.tag.toString().toInt()

}