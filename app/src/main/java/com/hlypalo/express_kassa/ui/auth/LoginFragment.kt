package com.hlypalo.express_kassa.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.AuthenticationRequest
import com.hlypalo.express_kassa.ui.activity.MainActivity
import com.hlypalo.express_kassa.util.PREF_TOKEN
import com.hlypalo.express_kassa.util.enqueue
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    private val api: ApiService by lazy { ApiService.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val span = buildSpannedString {
            append("Нет аккаунта? ")
            context?.let {
                color(ContextCompat.getColor(it, R.color.colorAccent)) {
                    append("Зарегистрироваться")
                }
            }
        }

        text_login_register?.text = span

        text_login_register?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit)
                ?.replace(R.id.container, RegisterFragment())
                ?.commit()
        }

        text_forgot_password?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                ?.replace(R.id.container, ForgotPasswordFragment())
                ?.addToBackStack(null)?.commit()
        }

        btn_login?.setOnClickListener {
            login()
        }
    }

    private fun validate() : Boolean {
        if (input_login_email?.text.isNullOrBlank()) {
            layout_login_email?.error = "Это поле не может быть пустым"
            return false
        } else {
            layout_login_email?.error = null
        }
        if (input_login_password?.text.isNullOrBlank()) {
            layout_login_password?.error = "Это поле не может быть пустым"
            return false
        } else {
            layout_login_password?.error = null
        }
        return true
    }

    private fun login() {
        if (!validate()) {
            return
        }

        val email = input_login_email?.text.toString()
        val password = input_login_password?.text.toString()
        val dto = AuthenticationRequest(email, password)
        api.login(dto).enqueue {
            onResponse = { resp ->
                App.prefEditor
                    .putString(PREF_TOKEN, resp)
                    .commit()
                context?.let {
                    startActivity(MainActivity.getStartIntent(it))
                }
                activity?.finish()
            }
            onError = {
                activity?.showError(it)
            }
        }
    }

}