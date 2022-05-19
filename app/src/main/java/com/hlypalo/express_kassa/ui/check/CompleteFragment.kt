package com.hlypalo.express_kassa.ui.check

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.repository.CheckRepository
import com.hlypalo.express_kassa.data.repository.MerchantRepository
import com.hlypalo.express_kassa.ui.settings.PrintersFragment
import com.hlypalo.express_kassa.util.CheckBuilder
import com.hlypalo.express_kassa.util.BluetoothPrinterUtil
import com.hlypalo.express_kassa.util.compressReceiptToFile
import com.hlypalo.express_kassa.util.getShareIntent
import kotlinx.android.synthetic.main.fragment_complete.*
import java.util.concurrent.Executors

class CompleteFragment : Fragment() {

    private val repo: CheckRepository by lazy { CheckRepository() }
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var check: Check

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_arrow_left_24)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)

        check = repo.getCheck()!!

        btn_complete?.setOnClickListener {
            repo.updateCheck(null)
            parentFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }

        printCheck()

        btn_print?.setOnClickListener {
            printCheck()
        }

        btn_settings?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                ?.replace(R.id.container, PrintersFragment(true))
                ?.addToBackStack(null)?.commit()
        }
    }

    private fun printCheck() {
        toggleProgress(true)
        executor.execute {
            try {
                BluetoothPrinterUtil.printCheck(
                    check = check,
                    view = view,
                    context = context
                )
                view?.post {
                    enableButton()
                    toggleProgress(false)
                }
                text_print_status?.visibility = View.INVISIBLE
            } catch (ex: Exception) {
                Log.e("TAG", ex.message, ex)
                view?.post {
                    toggleProgress(false)
                }
                text_print_status?.text = "Принтер не подключен"
            }
        }
    }

    private fun enableButton() {
        btn_print?.isEnabled = true
        btn_print?.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorAccent
            )
        )
    }

    private fun toggleProgress(f: Boolean) {
        if (f) {
            text_print_status?.text = "Чек печатается, пожалуйста подождите"
        }
        progressBar?.visibility = if (f) View.VISIBLE else View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
    }

    private fun shareCheck() {
        val file = CheckBuilder(context, check).build(null)
            .compressReceiptToFile(context)

        val shareIntent = requireContext().getShareIntent(file)
        startActivity(shareIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                return true
            }
            R.id.share -> {
                shareCheck()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}