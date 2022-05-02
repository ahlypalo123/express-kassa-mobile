package com.hlypalo.express_kassa.ui.product

import android.Manifest
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.ui.base.NavigationFragment
import kotlinx.android.synthetic.main.fragment_bar_code.*
import kotlinx.android.synthetic.main.fragment_bar_code.toolbar
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class BarCodeFragment : Fragment(), ZBarScannerView.ResultHandler {

    private val repo: ProductRepository by lazy { ProductRepository() }
    private val list: MutableList<Product> by lazy { mutableListOf() }

    private val requestCameraPermissions = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scanner_view?.setResultHandler(this)
            scanner_view?.startCamera()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bar_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

        list.clear()
        repo.fetchProductList {
            onResponse = func@{
                it ?: return@func
                list.addAll(it)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (parentFragment as? NavigationFragment)?.openDrawer()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        requestCameraPermissions.launch(Manifest.permission.CAMERA)
    }

    override fun handleResult(rawResult: Result?) {
        MediaPlayer.create(context, R.raw.beep).start()
        CoroutineScope(Dispatchers.IO).launch {
            list.firstOrNull { p -> p.barCode == rawResult?.contents }?.let { p ->
                repo.addProductToCheck(p)
            }
        }
        view?.postDelayed({
            scanner_view?.resumeCameraPreview(this)
        }, 2500)
    }
}