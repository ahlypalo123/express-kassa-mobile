package com.hlypalo.express_kassa.ui.check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.repository.CheckRepository
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.ui.base.NavigationFragment
import com.hlypalo.express_kassa.util.CheckBuilder
import com.hlypalo.express_kassa.util.CheckPrinterUtil
import com.hlypalo.express_kassa.util.enqueue
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_check.*
import kotlinx.android.synthetic.main.fragment_check.image_check
import kotlinx.android.synthetic.main.fragment_check.toolbar
import kotlinx.android.synthetic.main.fragment_check_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckFragment : Fragment() {

    private val repo: CheckRepository by lazy { CheckRepository() }
    private val productRepo: ProductRepository by lazy { ProductRepository() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_arrow_left_24)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)

        val check = productRepo.getCheck()
        CheckBuilder.build(check!!, context).let {
            image_check?.setImageBitmap(it)
        }

        btn_continue?.setOnClickListener {
            saveCheck()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveCheck() = CoroutineScope(Dispatchers.IO).launch {
//        val last4 = input_check_customer_number?.text.toString().toIntOrNull()
//        val name = input_check_customer_name?.text.toString()
//        val discount = input_check_discount?.text.toString().toFloatOrNull()

        val req = productRepo.getCheck()!!
        val check = repo.saveCheck(req) ?: return@launch
        productRepo.updateCheck(check)
        withContext(Dispatchers.Main) {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                ?.replace(R.id.container, CompleteFragment())
                ?.addToBackStack(null)?.commit()
        }
    }
}