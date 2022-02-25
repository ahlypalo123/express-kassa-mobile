package com.hlypalo.express_kassa.ui.product

import com.hlypalo.express_kassa.data.model.ErrorBody

interface ProductView {

    fun updateList()

    fun getFilter(): String?

    fun showError(err: ErrorBody?)

}