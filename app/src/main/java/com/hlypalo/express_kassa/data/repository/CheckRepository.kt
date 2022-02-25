package com.hlypalo.express_kassa.data.repository

import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.db.dao.CheckDao
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.util.PREF_LAST_CHECK_SAVED_DATE

// stores check records to the local database
// if has internet connection - also saves to the server
// when the Internet is turned on - saves unsaved data to the server
// each application launching - save unsaved data to the server and sync local data
class CheckRepository {

    private val dao: CheckDao by lazy { App.db.checkDao() }
    private val api: ApiService by lazy { ApiService.getInstance() }

    suspend fun saveCheck(data: Check) {
        dao.insert(data)
        api.saveCheckAsync(data).await()
        App.prefEditor.putLong(PREF_LAST_CHECK_SAVED_DATE, System.currentTimeMillis())
    }

    suspend fun getCheckHistory() : List<Check> {
        return dao.getAll()
    }

    suspend fun sync() {
        val date = App.sharedPrefs.getLong(
            PREF_LAST_CHECK_SAVED_DATE,
            System.currentTimeMillis())
        dao.getAllAfter(date).forEach {
            // TODO save checks in bulk
            api.saveCheckAsync(it).await()
        }
        dao.deleteAll()
        val resp = api.getCheckHistoryAsync().await()
        if (resp.isSuccessful) {
            resp.body()?.forEach {
                dao.insert(it)
            }
        }
    }

}