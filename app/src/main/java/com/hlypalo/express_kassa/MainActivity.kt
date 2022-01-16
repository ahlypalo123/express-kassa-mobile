package com.hlypalo.express_kassa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hlypalo.express_kassa.ui.auth.LoginFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, LoginFragment())
            .commit()
    }
}