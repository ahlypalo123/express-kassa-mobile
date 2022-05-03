package com.hlypalo.express_kassa.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.ErrorBody
import com.hlypalo.express_kassa.data.model.MerchantDetails
import com.hlypalo.express_kassa.data.model.ShiftRequest
import com.hlypalo.express_kassa.util.enqueue

class MerchantRepository private constructor() {

    private val api: ApiService by lazy { ApiService.getInstance() }
    private val liveData: MutableLiveData<MerchantDetails> by lazy { MutableLiveData() }

    companion object {
        val instance = MerchantRepository()
    }

    fun getMerchantDetails() : MerchantDetails? {
        return liveData.value
    }

    fun initMerchantDetails() : LiveData<MerchantDetails> {
        if (liveData.value == null) {
            api.getMerchantDetails().enqueue {
                onResponse = {
                    liveData.postValue(it)
                }
            }
        }
        return liveData
    }

    fun updateMerchantDetails(
        data: MerchantDetails,
        callback: () -> Unit,
        error: (ErrorBody?) -> Unit
    ) {
        api.updateMerchantDetails(data).enqueue {
            onResponse = {
                liveData.postValue(it)
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
                val details = liveData.value
                details?.shift = it
                liveData.postValue(details)
            }
            onError = {
                error(it)
            }
        }
    }

    fun closeShift(error: (ErrorBody?) -> Unit) {
        api.closeShift().enqueue {
            onResponse = {
                val details = liveData.value
                details?.shift = null
                liveData.postValue(details)
            }
            onError = {
                error(it)
            }
        }
    }

}