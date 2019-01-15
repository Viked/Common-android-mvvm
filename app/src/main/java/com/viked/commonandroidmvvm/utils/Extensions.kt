package com.viked.commonandroidmvvm.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.databinding.ObservableField
import android.net.Uri
import android.support.annotation.DrawableRes
import android.text.format.DateFormat
import com.viked.commonandroidmvvm.BaseApp
import com.viked.commonandroidmvvm.R
import kotlinx.coroutines.*
import java.util.*


/**
 * Created by yevgeniishein on 7/25/17.
 */
fun Double.cutDecimal(count: Int) = String.format(Locale.ROOT, "%.${count}f", this)

fun Context.openLink(link: String) {
    val i = Intent(ACTION_VIEW)
    i.data = Uri.parse(link)
    startActivity(i)
}

fun Context.sendEmail(email: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
            "mailto", email, null))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
    startActivity(Intent.createChooser(emailIntent, "Send email..."))
}

fun Context.shareApp() {
    val shareBody = "${getString(R.string.app_name)} ${getAppUrl()}"
    val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name))
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_with)))
}

fun Context.rateApp() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
    } catch (e: android.content.ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getAppUrl())))
    }
}

fun Date.formatDate(context: Context): String {
    return DateFormat.getDateFormat(context).format(this)

}

fun Date.formatTime(context: Context): String {
    return DateFormat.getTimeFormat(context).format(this)
}

private fun Context.getAppUrl() = "https://play.google.com/store/apps/details?id=$packageName"

fun <T> T.toObservable() = ObservableField<T>(this)

inline fun <T> T.doIf(value: Boolean, operation: (T) -> T) = let { if (value) operation.invoke(it) else it }

@DrawableRes
fun Context.getAndroidDrawable(resourceId: Int): Int {
    val attrs = theme.obtainStyledAttributes(intArrayOf(resourceId))
    val drawableResourceId = attrs.getResourceId(0, 0)
    attrs.recycle()
    return drawableResourceId
}

fun Context.getPreferenceHelper() = (applicationContext as BaseApp).preferenceHelper

fun <T> lazyPromise(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {
    return lazy {
        GlobalScope.async(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
    }
}