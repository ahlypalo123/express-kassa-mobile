package com.hlypalo.express_kassa.ui.check

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.ui.devices.PrintersFragment
import com.hlypalo.express_kassa.util.CheckBuilder
import com.hlypalo.express_kassa.util.CheckPrinterUtil
import com.hlypalo.express_kassa.util.compressReceiptToFile
import com.hlypalo.express_kassa.util.getShareIntent
import kotlinx.android.synthetic.main.fragment_complete.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.Executors

class CompleteFragment : Fragment() {

    private val repo: ProductRepository by lazy { ProductRepository() }
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
                CheckPrinterUtil.printCheck(
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
        progressBar?.visibility = if (f) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
    }

    private fun shareCheck() {
        val file = CheckBuilder.build(check, context, true)
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