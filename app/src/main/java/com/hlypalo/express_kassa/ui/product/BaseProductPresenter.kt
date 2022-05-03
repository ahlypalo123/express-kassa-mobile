package com.hlypalo.express_kassa.ui.product

import com.hlypalo.express_kassa.data.model.CheckProduct
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.CheckRepository

open class BaseProductPresenter {

    private val repo: CheckRepository by lazy { CheckRepository() }

    fun addProductToCheck(product: Product) {
        val check = repo.getCheck()
        var cp = check?.products?.find {
            it.name == product.name
        }
        if (cp != null) {
            cp.count++
        } else {
            cp = CheckProduct(
                name = product.name,
                count = 1,
                productId = product.id,
                price = product.price
            )
            check?.products?.add(cp)
        }
        check?.total = (check?.total ?: 0F) + product.price
        repo.updateCheck(check)
    }

}