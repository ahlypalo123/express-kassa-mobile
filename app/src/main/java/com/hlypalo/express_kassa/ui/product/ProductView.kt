package com.hlypalo.express_kassa.ui.product

import com.hlypalo.express_kassa.data.model.Product

interface ProductView {

    fun updateList()

    fun showEditProductDialog(product: Product)
}