package com.hlypalo.express_kassa.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.AuthenticationRequest
import com.hlypalo.express_kassa.util.ARG_EMAIL
import com.hlypalo.express_kassa.util.alert
import com.hlypalo.express_kassa.util.enqueue
import kotlinx.android.synthetic.main.fragment_update_password.*

class UpdatePasswordFragment(private var email: String?) : Fragment() {

    private val api: ApiService by lazy { ApiService.getInstance() }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(ARG_EMAIL, email)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (email == null) {
            email = savedInstanceState?.getString(ARG_EMAIL)
        }
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_update?.setOnClickListener {
            val password = input_new_password?.text.toString()
            api.updatePassword(
                AuthenticationRequest(password = password)
            ).enqueue {
                onResponse = {
                    activity?.alert("Пароль успешно изменен") {
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.container, LoginFragment())
                            ?.addToBackStack(null)?.commit()
                    }
                }
            }
        }
    }

}