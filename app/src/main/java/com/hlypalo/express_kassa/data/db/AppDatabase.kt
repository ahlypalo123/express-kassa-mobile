package com.hlypalo.express_kassa.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hlypalo.express_kassa.data.db.converter.DataConverter
import com.hlypalo.express_kassa.data.db.dao.CartDao
import com.hlypalo.express_kassa.data.db.dao.CheckDao
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Check

@Database(entities = [ CartProduct::class, Check::class ], version = 4)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun checkDao() : CheckDao

    abstract fun cartDao() : CartDao

}