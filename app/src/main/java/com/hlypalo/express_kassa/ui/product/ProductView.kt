package com.hlypalo.express_kassa.ui.product

import com.hlypalo.express_kassa.data.model.ErrorBody

interface ProductView {

    fun updateList()

    fun showError(err: ErrorBody?)

    fun manageEmpty(b: Boolean)

    fun showProgress()

    fun hideProgress()

}