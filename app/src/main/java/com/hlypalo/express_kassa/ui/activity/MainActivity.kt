package com.hlypalo.express_kassa.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.ui.auth.LoginFragment
import com.hlypalo.express_kassa.ui.base.NavigationFragment
import com.hlypalo.express_kassa.util.PREF_TOKEN

class MainActivity : AppCompatActivity() {

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = if (App.sharedPrefs.getString(PREF_TOKEN, "").isNullOrBlank()) {
            LoginFragment()
        } else {
            NavigationFragment()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}