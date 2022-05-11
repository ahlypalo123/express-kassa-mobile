package com.hlypalo.express_kassa.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.ShiftRequest
import com.hlypalo.express_kassa.data.repository.MerchantRepository
import com.hlypalo.express_kassa.ui.main.NavigationFragment
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_shift.*

class ShiftFragment : Fragment() {

    private val repo: MerchantRepository by lazy { MerchantRepository.instance }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shift, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

        updateUi()
        repo.initMerchantDetails().observe(viewLifecycleOwner) {
            updateUi()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val fragment = activity?.supportFragmentManager
                    ?.findFragmentByTag(NavigationFragment::class.java.simpleName)
                (fragment as? NavigationFragment)?.openDrawer()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateEmployeeName() : Boolean {
        if (layout_employee_name?.isEmpty() == true) {
            layout_employee_name?.error = "Имя работника не может быть пустым"
            return false
        }
        layout_employee_name?.error = null
        return true
    }

    private fun updateUi() {
        val shift = repo.getMerchantDetails()?.shift
        input_employee_name?.isEnabled = shift == null

        if (shift == null) {
            btn_manage_shift?.text = "Открыть смену"
            btn_manage_shift?.setOnClickListener {
                openShift()
            }
        } else {
            input_employee_name?.setText(shift.employeeName)
            btn_manage_shift?.text = "Закрыть смену"
            btn_manage_shift?.setOnClickListener {
                closeShift()
            }
        }
    }

    private fun openShift() {
        if (validateEmployeeName()) {
            val req = ShiftRequest(employee_name = input_employee_name?.text?.toString()!!)
            repo.openShift(req, error = {
                activity?.showError(it)
            })
        }
    }

    private fun closeShift() {
        repo.closeShift(error = {
            activity?.showError(it)
        })
    }

}