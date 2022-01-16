package com.hlypalo.express_kassa.ui.auth

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.AuthenticationDto
import com.hlypalo.express_kassa.util.appendClickable
import com.hlypalo.express_kassa.util.enqueue
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    private val api: ApiService by lazy { ApiService.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sb = SpannableStringBuilder()
        sb.append("Уже зарегистрированы? ")
        sb.appendClickable("Войти") {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, LoginFragment())
                ?.addToBackStack(null)?.commit()
        }
        text_login_register.setText(sb, TextView.BufferType.SPANNABLE)

        btn_register?.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val email = input_register_email?.text.toString()
        val password = input_register_password?.text.toString()
        val passwordConfirmation = input_register_password_confirmation?.text.toString()
        if (password != passwordConfirmation) {
            input_register_password_confirmation?.error = "Пароли не совпадают"
            return
        }
        val dto = AuthenticationDto(email, password)
        api.register(dto).enqueue {
            // TODO
        }
    }
}