//package com.hlypalo.express_kassa.data.db.dao
//
//import androidx.lifecycle.LiveData
//import androidx.room.*
//import com.hlypalo.express_kassa.data.model.CartDto
//import com.hlypalo.express_kassa.data.model.CartProduct
//import com.hlypalo.express_kassa.data.model.Product
//
//@Dao
//interface CartDao {
//
//    @Query("SELECT name, price, COUNT(*) as count FROM cartproduct GROUP BY name, price")
//    fun getAllGroupedLiveData() : LiveData<List<CartDto>>
//
//    @Query("SELECT name, price, COUNT(*) as count FROM cartproduct GROUP BY name, price")
//    suspend fun getAllGrouped() : List<CartDto>
//
//    @Query("SELECT * FROM cartproduct")
//    suspend fun getAll() : List<CartProduct>
//
//    @Insert
//    suspend fun add(data: CartProduct)
//
//    @Delete
//    suspend fun delete(data: CartProduct)
//
//    @Update
//    suspend fun update(data: CartProduct)
//
//    @Query("DELETE FROM cartproduct where name = :name")
//    suspend fun deleteByName(name: String)
//
//    @Query("DELETE FROM cartproduct")
//    suspend fun deleteAll()
//}