package com.hlypalo.express_kassa.ui.product

import android.Manifest
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.util.PathUtil
import kotlinx.android.synthetic.main.fragmet_add_product.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.File

class AddProductFragment : Fragment() {

    private val repo: ProductRepository by lazy { ProductRepository() }
    private var currentPhotoPath: String = ""

    private val requestCameraPermissions = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePicture()
        }
    }

    private val requestStorePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requestGetContent.launch("image/*")
        }
    }

    private val requestTakePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { result ->
        if (result) {
            updateImage()
        }
    }

    private val requestGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result ->
        result ?: return@registerForActivityResult
        currentPhotoPath = PathUtil.getPath(context, result)
        updateImage()
    }

    private fun updateImage() {
        image_product?.setImageBitmap(
            BitmapFactory.decodeFile(currentPhotoPath)
        )
    }

    private fun dispatchTakePicture() {
        val photoFile = createImageFile()
        val uri = FileProvider.getUriForFile(
            requireActivity(),
            "com.hlypalo.express_kassa.fileprovider",
            photoFile
        )
        requestTakePicture.launch(uri)
    }

    private fun createImageFile(): File {
        val timeStamp = DateTime.now().toString("yyyyMMdd_HHmmss")
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply  {
            currentPhotoPath = absolutePath
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragmet_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_product_add?.setOnClickListener {
            saveProduct()
        }

        image_product?.setOnClickListener {
            openPhotoOptionsDialog()
        }
    }

    val barCode = ""

    private fun saveProduct() {
        val product = Product(
            0,
            name = input_product_name?.text.toString(),
            price = input_product_price?.text.toString().toFloat(),
            null,
            barCode = barCode
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (currentPhotoPath.isNotBlank()) {
                repo.uploadPhoto(File(currentPhotoPath))?.let {
                    product.photoUrl = ApiService.BASE_URL + it
                }
            }
            repo.addProduct(product)
            withContext(Dispatchers.Main) {
                activity?.onBackPressed()
            }
        }
    }

    private fun openPhotoOptionsDialog() {
        val options = resources.getStringArray(R.array.edit_photo_options)

        val adapter = activity?.let { it1 ->
            ArrayAdapter(
                it1, android.R.layout.simple_list_item_1, options
            )
        }

        AlertDialog.Builder(requireActivity()).setAdapter(adapter) { _, which ->
            when (which) {
                0 -> {
                    requestStorePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                1 -> {
                    requestCameraPermissions.launch(Manifest.permission.CAMERA)
                }
            }
        }.create().show()
    }

}