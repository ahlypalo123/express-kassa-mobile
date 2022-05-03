package com.hlypalo.express_kassa.ui.main

import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.model.CheckProduct
import com.hlypalo.express_kassa.data.repository.CheckRepository
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.util.enqueue

class MainPresenter(
    private val view: MainView,
) {

    private var check: Check? = null
    private val repo: CheckRepository by lazy { CheckRepository() }
    private val api: ApiService by lazy { ApiService.getInstance() }

    fun init() {
        check = repo.getCheck()
        if (check == null || check!!.completed) {
            check = Check()
            repo.updateCheck(check)
        }
        repo.subscribeOnCheck {
            check = it
            refresh()
        }
        refresh()
    }

    fun onPay() {
        syncCheck()
    }

    private fun syncCheck() {
        val check = repo.getCheck()
        if (check?.id == null || check.completed) {
            createCheck()
        } else {
            view.toggleProgress(true)
            api.deleteCheck(check.id).enqueue {
                onResponse = {
                    createCheck()
                    view.toggleProgress(false)
                }
                onError = {
                    view.showError(it)
                    view.toggleProgress(false)
                }
            }
        }
    }

    private fun createCheck() {
        view.toggleProgress(true)
        api.createCheck(check!!).enqueue {
            onResponse = {
                check = it
                repo.updateCheck(check)
                view.toggleProgress(false)
                view.showPaymentMethodDialog()
            }
            onError = {
                view.showError(it)
                view.toggleProgress(false)
            }
        }
    }

    fun unsubscribe() {
        repo.unsubscribeFromCheck()
    }

    private fun refresh() {
        view.manageCheckEmpty(check?.products?.isEmpty() == true)
        view.updateCheck()
        view.updateTotal(check?.total)
    }

    fun getProductListForCheck(): List<CheckProduct> {
        return check?.products ?: listOf()
    }

    fun deleteFromCart(pos: Int) {
        check ?: return
        val product = check!!.products[pos]
        check!!.products.removeAt(pos)
        check!!.total = check!!.total?.minus(product.count * product.price)
        repo.updateCheck(check)
    }

}