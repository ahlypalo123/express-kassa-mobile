package com.hlypalo.express_kassa.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.*
import com.hlypalo.express_kassa.util.enqueue

class MerchantRepository private constructor() {

    private val api: ApiService by lazy { ApiService.getInstance() }
    private val merchantLiveData: MutableLiveData<MerchantDetails> by lazy { MutableLiveData() }
    private val templateLiveData: MutableLiveData<ReceiptTemplateData> by lazy { MutableLiveData() }

    companion object {
        val instance = MerchantRepository()
    }

    fun getMerchantDetails() : MerchantDetails? {
        return merchantLiveData.value
    }

    fun clearCache() {
        merchantLiveData.postValue(null)
    }

    fun initMerchantDetails() : LiveData<MerchantDetails> {
        if (merchantLiveData.value == null) {
            api.getMerchantDetails().enqueue {
                onResponse = {
                    merchantLiveData.postValue(it)
                }
            }
        }
        return merchantLiveData
    }

    fun updateMerchantDetails(
        data: MerchantDetails,
        callback: () -> Unit,
        error: (ErrorBody?) -> Unit
    ) {
        api.updateMerchantDetails(data).enqueue {
            onResponse = {
                merchantLiveData.postValue(it)
                callback()
            }
            onError = {
                error(it)
            }
        }
    }

    fun openShift(
        req: ShiftRequest,
        error: (ErrorBody?) -> Unit
    ) {
        api.openShift(req).enqueue {
            onResponse = {
                val details = merchantLiveData.value
                details?.shift = it
                merchantLiveData.postValue(details)
            }
            onError = {
                error(it)
            }
        }
    }

    fun closeShift(error: (ErrorBody?) -> Unit) {
        api.closeShift().enqueue {
            onResponse = {
                val details = merchantLiveData.value
                details?.shift = null
                merchantLiveData.postValue(details)
            }
            onError = {
                error(it)
            }
        }
    }

    fun initTemplate(error: (ErrorBody?) -> Unit) : LiveData<ReceiptTemplateData> {
        if (templateLiveData.value == null) {
            api.getReceiptTemplate().enqueue {
                onResponse = {
                    templateLiveData.postValue(it?.data)
                }
                onError = {
                    error(it)
                }
            }
        }
        return templateLiveData
    }

    fun setActiveTemplate(
        template: ReceiptTemplate,
        callback: () -> Unit,
        error: (ErrorBody?) -> Unit
    ) {
        api.setActiveTemplate(template.id).enqueue {
            onResponse = {
                templateLiveData.postValue(template.data)
                callback()
            }
            onError = {
                error(it)
            }
        }
    }

    fun getTemplate() : ReceiptTemplateData? {
        return templateLiveData.value
    }
}