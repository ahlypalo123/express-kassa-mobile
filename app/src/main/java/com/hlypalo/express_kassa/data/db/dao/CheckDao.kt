package com.hlypalo.express_kassa.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hlypalo.express_kassa.data.model.Check

@Dao
interface CheckDao {

    @Insert
    suspend fun insert(data: Check)

    @Query("SELECT * FROM `check`")
    suspend fun getAll() : List<Check>

    @Query("SELECT * FROM `check` where date > :date")
    suspend fun getAllAfter(date: Long) : List<Check>

    @Query("DELETE FROM `check`")
    suspend fun deleteAll()

}