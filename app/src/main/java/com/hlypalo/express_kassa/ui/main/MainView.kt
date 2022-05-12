package com.hlypalo.express_kassa.ui.main

import com.hlypalo.express_kassa.data.model.ErrorBody

interface MainView {

    fun toggleProgress(f: Boolean)

    fun startPayment()

    fun updateTotal(total: Float?)

    fun showError(err: ErrorBody?)

}