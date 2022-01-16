package com.hlypalo.express_kassa.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.CartProduct

@Dao
interface CartDao {

    @Query("SELECT name, price, COUNT(*) as count FROM cartproduct GROUP BY name, price")
    fun getAllWithCountLiveData() : LiveData<List<CartDto>>

    @Query("SELECT name, price, COUNT(*) as count FROM cartproduct GROUP BY name, price")
    suspend fun getAllWithCount() : List<CartDto>

    @Insert
    suspend fun add(data: CartProduct)

    @Delete
    suspend fun delete(data: CartProduct)

    @Update
    suspend fun update(data: CartProduct)
}