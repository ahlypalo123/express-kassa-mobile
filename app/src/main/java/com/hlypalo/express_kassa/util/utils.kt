package com.hlypalo.express_kassa.util

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.load.engine.DiskCacheStrategy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG = "Utils"

class CallBackKt<T> : Callback<T> {

    var onResponse: ((T?) -> Unit)? = null
    var onError: ((T?) -> Unit)? = null
    var onFailure: ((t: Throwable?) -> Unit)? = null

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.d(TAG, t.message ?: "")
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onResponse?.invoke(response.body())
        } else {
            onError?.invoke(response.body())
        }
    }

}

fun <T> Call<T>.enqueue(callback: CallBackKt<T>.() -> Unit) {
    val callBackKt = CallBackKt<T>()
    callback.invoke(callBackKt)
    this.enqueue(callBackKt)
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun SpannableStringBuilder.append(
    context: Context?,
    text: CharSequence,
    @DimenRes textSizeRes: Int,
    styleSpan: StyleSpan? = null
) {
    val index = length
    append(text)
    if (styleSpan != null)
        setSpan(styleSpan, index, index + text.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    setSpan(
        AbsoluteSizeSpan(context?.resources?.getDimensionPixelSize(textSizeRes) ?: return),
        index,
        index + text.length,
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
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

fun ImageView.loadUrlNoCache(url: String?, placeholder: Int) {
//    GlideApp.with(context).load(url?.trim())
//        .diskCacheStrategy(DiskCacheStrategy.NONE)
//        .skipMemoryCache(true)
//        .placeholder(placeholder)
//        .into(this)
}