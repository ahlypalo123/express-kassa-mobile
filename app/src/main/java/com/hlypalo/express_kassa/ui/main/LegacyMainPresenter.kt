package com.hlypalo.express_kassa.ui.main

import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.repository.CheckRepository
import com.hlypalo.express_kassa.data.repository.MerchantRepository
import com.hlypalo.express_kassa.util.enqueue

open class LegacyMainPresenter(
    private val view: MainView
) {

    var check: Check? = null
    protected val repo: CheckRepository by lazy { CheckRepository() }
    protected val api: ApiService by lazy { ApiService.getInstance() }

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

    fun updateCheck(check: Check) {
        repo.updateCheck(check)
    }

    open fun refresh() {
        view.updateTotal(check?.total)
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
                check?.date = System.currentTimeMillis()
                repo.updateCheck(check)
                view.toggleProgress(false)
                view.startPayment()
            }
            onError = {
                view.showError(it)
                view.toggleProgress(false)
            }
        }
    }

}