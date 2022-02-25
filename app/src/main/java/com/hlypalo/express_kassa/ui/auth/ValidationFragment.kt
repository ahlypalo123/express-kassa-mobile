package com.hlypalo.express_kassa.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.ValidateRequest
import com.hlypalo.express_kassa.util.ARG_EMAIL
import com.hlypalo.express_kassa.util.enqueue
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_validation.*

class ValidationFragment(private var email: String?) : Fragment() {

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
        return inflater.inflate(R.layout.fragment_validation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_send?.setOnClickListener {
            api.validate(ValidateRequest(
                email = email,
                code = input_confirmation_code?.text.toString().toInt()
            )).enqueue {
                onResponse = {
                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.container, UpdatePasswordFragment(email))
                        ?.addToBackStack(null)?.commit()
                }
                onError = {
                    activity?.showError(it)
                }
            }
        }
    }

}