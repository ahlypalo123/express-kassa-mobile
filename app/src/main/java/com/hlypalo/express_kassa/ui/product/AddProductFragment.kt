package com.hlypalo.express_kassa.ui.product

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.util.PathUtil
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.dialog_add_product.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.File

class AddProductFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_GALLERY = 3
        private const val REQUEST_CODE_CAMERA = 4
    }

    private val repo: ProductRepository by lazy { ProductRepository() }

    private val requestCameraPermissions = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCameraIntent()
        }
    }

    private val requestStorePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startPictureSelection()
        }
    }

    private val requestTakePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { result ->
    }

    private fun startTakePicture() {
        val uri = createTemp
        requestTakePicture.la
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_product_add?.setOnClickListener {
            saveProduct()
        }

        image_product?.setOnClickListener {
            startTakePhotoDialog()
        }
    }

    private fun saveProduct() {
        val name = input_product_name?.text.toString()
        val price = input_product_price?.text.toString().toFloat();
        val product = Product(0, name, price, null)
        CoroutineScope(Dispatchers.IO).launch {
            repo.addProduct(product)
            withContext(Dispatchers.Main) {
                activity?.onBackPressed()
            }
        }
    }

    private fun startTakePhotoDialog() {
        val options = resources.getStringArray(R.array.edit_photo_options)

        val adapter = activity?.let { it1 ->
            ArrayAdapter(
                it1, android.R.layout.simple_list_item_1, options
            )
        }

        AlertDialog.Builder(requireActivity()).setAdapter(adapter) { _, which ->
            when (which) {
                0 -> {
                    requestCameraPermissions.launch(Manifest.permission.CAMERA)
                }
                1 -> {
                    requestStorePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }.create().show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    }

    private fun getSelectPictureIntent() : Intent {
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        return Intent.createChooser(galleryIntent, "Select Picture")
    }

}