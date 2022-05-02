package com.hlypalo.express_kassa.ui.main

import com.hlypalo.express_kassa.data.model.ErrorBody

interface MainView {

    fun updateCheck()

    fun updateTotal(total: Float?)

    fun manageCheckEmpty(b: Boolean)

    fun openCheckFragment()

    fun showChangeDialog()

    fun showError(err: ErrorBody?)

    fun toggleProgress(f: Boolean)

    fun showPaymentMethodDialog()

}