package com.hlypalo.express_kassa.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.AuthenticationRequest
import com.hlypalo.express_kassa.util.alert
import com.hlypalo.express_kassa.util.enqueue
import com.hlypalo.express_kassa.util.showError
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
        val span = buildSpannedString {
            append("Уже зарегистрированы? ")
            context?.let {
                color(ContextCompat.getColor(it, R.color.design_default_color_primary)) {
                    append("Войти")
                }
            }
        }

        text_register_login?.text = span

        text_register_login?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, LoginFragment())
                ?.addToBackStack(null)?.commit()
        }

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
        val dto = AuthenticationRequest(email, password)
        api.register(dto).enqueue {
            onResponse = {
                activity?.alert("User was registered successfully")
            }
            onError = {
                activity?.showError(it)
            }
        }
    }
}