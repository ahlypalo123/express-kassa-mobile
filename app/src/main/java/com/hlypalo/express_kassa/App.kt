package com.hlypalo.express_kassa

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences

class App : Application() {

    companion object {
        private const val PREFS = "com.hlypalo.express_kassa"

        lateinit var sharedPrefs: SharedPreferences
            private set
        lateinit var prefEditor: SharedPreferences.Editor
            private set
//        lateinit var db: AppDatabase
//            private set
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        prefEditor = sharedPrefs.edit()

//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "express_kassa"
//        ).fallbackToDestructiveMigration().build()
    }
}