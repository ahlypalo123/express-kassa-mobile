package com.hlypalo.express_kassa.ui.main

interface MainProductView : MainView {

    fun updateCheck()

    fun manageCheckEmpty(b: Boolean)

    fun openCheckFragment()

    fun showChangeDialog()

}