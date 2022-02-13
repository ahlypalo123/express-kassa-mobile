package com.hlypalo.express_kassa.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hlypalo.express_kassa.data.db.dao.CartDao
import com.hlypalo.express_kassa.data.db.dao.CheckDao
import com.hlypalo.express_kassa.data.model.CartProduct

@Database(entities = [ CartProduct::class ], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cartDao() : CartDao

    abstract fun checkDao() : CheckDao

}