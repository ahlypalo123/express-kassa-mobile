package com.hlypalo.express_kassa.util

import android.content.Context
import android.graphics.Bitmap
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.GlideApp
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.ErrorBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.ContextCompat.getSystemService

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import org.joda.time.DateTime
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.InetAddress
import java.util.*


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

fun List<CartDto>?.calculateTotal() : Float {
    this ?: return 0F
    return map { p -> p.price * p.count }.sum()
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
) = this?.alert(error?.message ?: error?.error, onOk = onOk)

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