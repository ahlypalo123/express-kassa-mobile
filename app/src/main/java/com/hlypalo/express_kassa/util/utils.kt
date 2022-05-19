package com.hlypalo.express_kassa.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Insets
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.util.Size
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonParser
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.GlideApp
import com.hlypalo.express_kassa.data.model.CheckProduct
import com.hlypalo.express_kassa.data.model.ErrorBody
import com.hlypalo.express_kassa.data.model.Product
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.*
import android.view.WindowInsets

import android.view.WindowMetrics
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


const val TAG = "Utils"

class CallBackKt<T> : Callback<T> {

    var onResponse: ((T?) -> Unit)? = null
    var onError: ((ErrorBody?) -> Unit)? = null
    var finally: (() -> Unit)? = null

    override fun onFailure(call: Call<T>, t: Throwable) {
        val err = ErrorBody(error = t.message ?: "Unknown error", message = t.localizedMessage)
        onError?.invoke(err)
        finally?.invoke()
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onResponse?.invoke(response.body())
        } else {
            response.errorBody()?.string()?.let {
                val message = try {
                    val json = JsonParser().parse(it).asJsonObject
                    if (json.has("message")) {
                        json.get("message").asString
                    } else {
                        "Unknown error"
                    }
                } catch (ex: Exception) {
                    "Unknown error"
                }
                val err = ErrorBody(error = message, message = "")
                onError?.invoke(err)
            }
        }
        finally?.invoke()
    }

}

fun <T> Call<T>.enqueue(callback: CallBackKt<T>.() -> Unit) {
    val callBackKt = CallBackKt<T>()
    callback(callBackKt)
    this.enqueue(callBackKt)
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun SpannableStringBuilder.appendClickable(
    text: CharSequence,
    clickListener: () -> Unit
) {
    val index = length
    append(text)
    val span = object : ClickableSpan() {
        override fun onClick(widget: View) {
            clickListener()
        }
    }
    setSpan(span, index, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
}

fun ImageView.loadImageUrl(url: String?, placeholder: Int) {
    GlideApp.with(context).load(url?.trim())
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .placeholder(placeholder)
        .into(this)
}

fun Context?.alert(
    message: Any?,
    title: Any? = null,
    onOk: (() -> Unit)? = null
) {
    if (this == null)
        return
    val builder = AlertDialog.Builder(this, R.style.CommonDialog)
    if (message is Int)
        builder.setMessage(message)
    if (message is String?)
        builder.setMessage(message)
    if (title != null) {
        if (title is Int)
            builder.setTitle(title)
        if (title is String)
            builder.setTitle(title)
    }

    builder.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss(); onOk?.invoke() }
    builder.create().show()
}

fun Context?.showError(
    error: ErrorBody?,
    onOk: (() -> Unit)? = null
) = this?.alert(error?.error, onOk = onOk)

fun Context.isNetworkAvailable() =
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

fun Bitmap.compressToFile(context: Context?) : File {
    val timeStamp = DateTime.now().toString("yyyyMMdd_HHmmss")
    val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val temp = File.createTempFile(
        "JPEG_${timeStamp}_COMPRESSED",
        ".jpg",
        storageDir
    )
    val fos = FileOutputStream(temp)
    compress(Bitmap.CompressFormat.JPEG, 30, fos)
    return temp
}

fun Context?.confirm(
    message: Any?,
    onYes: (() -> Unit)? = null,
    onNo: (() -> Unit)? = null,
    yesText: String? = null,
    notCancelable: Boolean? = null
) {
    if (this == null)
        return
    val builder = AlertDialog.Builder(this, R.style.CommonDialog)
    if (message is Int)
        builder.setMessage(message)
    if (message is String?)
        builder.setMessage(message)
    builder.setPositiveButton(
        yesText
    ) { dialog, _ -> dialog.dismiss(); onYes?.invoke() }
    if (notCancelable == true) {
        builder.setCancelable(false)
    }

    builder.setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss(); onNo?.invoke() }
    val dialog = builder.create()

    if (notCancelable == true) {
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    dialog.show()
}

fun Activity?.isAlive() = this != null && !this.isFinishing

fun Activity?.showCarouselDialog(
    value: Int,
    converter: () -> Array<String>,
    title: String? = null,
    callback: (Int) -> Unit
): AlertDialog? {
    if (!isAlive()) return null
    val values = converter.invoke()
    val builder = AlertDialog.Builder(this ?: return null, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
    @SuppressLint("InflateParams") val view =
        this.layoutInflater.inflate(R.layout.dialog_carousel, null)
    if (title != null) {
        val titleView = view?.findViewById<TextView>(R.id.text_title)
        titleView?.text = title
    }
    val numberPicker = view?.findViewById<NumberPicker>(R.id.np) ?: return null
    numberPicker.minValue = 0
    numberPicker.maxValue = values.size - 1
    numberPicker.displayedValues = values
    numberPicker.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
    numberPicker.value = value
    builder.setView(view)
    builder.setPositiveButton("Choose") { d, _ ->
        callback.invoke(numberPicker.value)
        d.dismiss()
    }
    builder.setNegativeButton(android.R.string.cancel) { d, _ -> d.dismiss() }
    val dialog = builder.create()
    dialog.window?.setBackgroundDrawableResource(R.color.white)
    dialog.setOnShowListener {
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.gray_scorpion))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
    }
    dialog.show()
    return dialog
}

fun Context.getShareIntent(file: File) : Intent {
    return Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            putExtra(
//                Intent.EXTRA_SUBJECT,
//                "Sharing file from the AppName"
//            )
//            putExtra(
//                Intent.EXTRA_TEXT,
//                "Sharing file from the AppName with some description"
//            )
        val fileURI = FileProvider.getUriForFile(
            this@getShareIntent, "com.hlypalo.express_kassa.fileprovider",
            file
        )
        putExtra(Intent.EXTRA_STREAM, fileURI)
    }
}

fun Bitmap.compressReceiptToFile(context: Context?) : File {
    val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val temp = File.createTempFile(
        "Receipt_${UUID.randomUUID()}",
        ".jpeg",
        storageDir
    )
    val fos = FileOutputStream(temp)
    compress(Bitmap.CompressFormat.JPEG, 100, fos)
    return temp
}

fun FragmentManager.getCallerFragment(): Fragment? =
    findFragmentByTag(getBackStackEntryAt(backStackEntryCount - 2).name)

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics)

val Number.toDp get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_PX,
    this.toFloat(),
    Resources.getSystem().displayMetrics)

