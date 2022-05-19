package com.hlypalo.express_kassa.ui.main

import com.hlypalo.express_kassa.data.model.CheckProduct

class MainProductPresenter(
    private val view: MainProductView,
) : LegacyMainPresenter(view) {

    fun unsubscribe() {
        repo.unsubscribeFromCheck()
    }

    override fun refresh() {
        view.manageCheckEmpty(check?.products?.isEmpty() == true)
        view.updateCheck()
        view.updateTotal(check?.total)
    }

    fun getProductListForCheck(): List<CheckProduct> {
        return check?.products ?: listOf()
    }

    fun deleteFromCheck(pos: Int) {
        check ?: return
        val product = check!!.products[pos]
        check!!.products.removeAt(pos)
        check!!.total = check!!.total?.minus(product.count * product.price)
        repo.updateCheck(check)
    }

}