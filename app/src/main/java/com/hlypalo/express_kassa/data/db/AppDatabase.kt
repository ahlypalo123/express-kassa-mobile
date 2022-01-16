package com.hlypalo.express_kassa.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hlypalo.express_kassa.data.db.dao.CartDao
import com.hlypalo.express_kassa.data.db.dao.ProductDao
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Product

@Database(entities = [ Product::class, CartProduct::class ], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun cartDao(): CartDao

}