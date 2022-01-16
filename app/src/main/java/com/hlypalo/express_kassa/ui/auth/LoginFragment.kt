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
import com.hlypalo.express_kassa.ui.main.MainFragment
import com.hlypalo.express_kassa.ui.main.NavigationFragment
import com.hlypalo.express_kassa.util.appendClickable
import com.hlypalo.express_kassa.util.enqueue
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
        val sb = SpannableStringBuilder()
        sb.append("Нет аккаунта? ")
        sb.appendClickable("Зарегистрироваться") {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, RegisterFragment())
                ?.addToBackStack(null)?.commit()
        }
        text_login_register.setText(sb, TextView.BufferType.SPANNABLE)

        btn_login?.setOnClickListener {
            val email = input_login_email?.text.toString()
            val password = input_login_password?.text.toString()
            val dto = AuthenticationDto(email, password)
            api.login(dto).enqueue {
                // TODO
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.container, NavigationFragment())
                    ?.addToBackStack(null)?.commit()
            }
        }
    }

}