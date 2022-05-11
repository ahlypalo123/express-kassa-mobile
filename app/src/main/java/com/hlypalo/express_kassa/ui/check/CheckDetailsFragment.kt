package com.hlypalo.express_kassa.ui.check

import android.graphics.*
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.util.*
import kotlinx.android.synthetic.main.fragment_check_details.*
import kotlinx.android.synthetic.main.fragment_check_details.image_check
import kotlinx.android.synthetic.main.fragment_check_details.toolbar


class CheckDetailsFragment(private var check: Check) : Fragment() {

    private lateinit var bitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (savedInstanceState?.getSerializable("check") as? Check)?.let {
            check = it
        }
        return inflater.inflate(R.layout.fragment_check_details, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("check", check)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_arrow_left_24)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)

        bitmap = CheckBuilder.build(check, context)
        image_check?.setImageBitmap(bitmap)
        btn_print?.setOnClickListener {
            CheckPrinterUtil.printCheck(check, view, context)
        }
    }

    private fun home() {
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                home()
                return true
            }
            R.id.share -> {
                requireContext().getShareIntent(bitmap.compressReceiptToFile(context))
            }
        }
        return super.onOptionsItemSelected(item)
    }

}