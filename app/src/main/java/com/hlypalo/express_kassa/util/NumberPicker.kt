package com.hlypalo.express_kassa.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText

class NumberPicker(context: Context, attrs: AttributeSet) : android.widget.NumberPicker(context, attrs) {

    override fun addView(child: View) {
        super.addView(child)
        updateView(child)
    }

    override fun addView(child: View, index: Int, params: android.view.ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        updateView(child)
    }

    override fun addView(child: View, params: android.view.ViewGroup.LayoutParams) {
        super.addView(child, params)
        updateView(child)
    }

    private fun updateView(view: View) {
        if (view is EditText) {
            view.textSize = 25f
        }
    }

}