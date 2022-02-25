package com.hlypalo.express_kassa.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.AuthenticationRequest
import com.hlypalo.express_kassa.util.enqueue
import kotlinx.android.synthetic.main.fragment_forgot_password.*

class ForgotPasswordFragment : Fragment() {

    private val api: ApiService by lazy { ApiService.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_send?.setOnClickListener {
            val email = input_forgot_email?.text.toString()

            api.forgotPassword(
                AuthenticationRequest(email)
            ).enqueue {
                onResponse = {
                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.container, ValidationFragment(email = email))
                        ?.addToBackStack(null)?.commit()
                }
            }
        }
    }
}