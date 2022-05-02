package com.hlypalo.express_kassa.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hlypalo.express_kassa.data.db.converter.DataConverter
import com.hlypalo.express_kassa.data.db.dao.CheckDao
import com.hlypalo.express_kassa.data.model.Check

@Database(entities = [ Check::class ], version = 5)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun checkDao() : CheckDao

    // abstract fun cartDao() : CartDao

}