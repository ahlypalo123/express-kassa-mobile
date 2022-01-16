package com.hlypalo.express_kassa.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hlypalo.express_kassa.data.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    suspend fun getAll() : List<Product>

    @Query("SELECT * FROM product")
    fun getAllLiveData() : LiveData<List<Product>>

    @Insert
    suspend fun add(data: Product)

    @Delete
    suspend fun delete(data: Product)

    @Update
    suspend fun update(data: Product)
}