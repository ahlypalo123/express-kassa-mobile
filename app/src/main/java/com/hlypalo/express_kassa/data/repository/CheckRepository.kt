package com.hlypalo.express_kassa.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.model.ErrorBody
import com.hlypalo.express_kassa.data.model.OrderColumn
import com.hlypalo.express_kassa.util.PREF_CHECK
import com.hlypalo.express_kassa.util.PREF_LAST_CHECK_SAVED_DATE
import com.hlypalo.express_kassa.util.enqueue

// stores check records to the local database
// if has internet connection - also saves to the server
// when the Internet is turned on - saves unsaved data to the server
// each application launching - save unsaved data to the server and sync local data
class CheckRepository {

    private val api: ApiService by lazy { ApiService.getInstance() }
    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    suspend fun saveCheck(data: Check) : Check? {
        // dao.insert(data)
        val resp = api.updateCheckAsync(data).await()
        if (resp.isSuccessful) {
            App.prefEditor.putLong(PREF_LAST_CHECK_SAVED_DATE, System.currentTimeMillis())
        }
        return resp.body()
    }

    fun getCheckHistory(
        orderColumn: OrderColumn,
        callback: (List<Check>?) -> Unit,
        error: (ErrorBody?) -> Unit
    ) {
        api.getCheckHistory(orderColumn).enqueue {
            onResponse = {
                callback(it)
            }
            onError = {
                error(it)
            }
        }
    }

    fun updateCheck(check: Check?) {
        App.prefEditor.putString(PREF_CHECK, Gson().toJson(check)).commit()
    }

    fun clearCache() {
        App.prefEditor.remove(PREF_CHECK).commit()
    }

    fun getCheck() : Check? {
        val check = App.sharedPrefs.getString(PREF_CHECK, "")
        if (check.isNullOrBlank()) {
            return null
        }
        return Gson().fromJson(check, Check::class.java)
    }

    fun subscribeOnCheck(callback: (Check?) -> Unit) {
        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_CHECK) {
                callback(getCheck())
            }
        }
        App.sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unsubscribeFromCheck() {
        App.sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

}