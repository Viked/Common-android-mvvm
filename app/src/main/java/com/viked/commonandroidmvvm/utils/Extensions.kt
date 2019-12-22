package com.viked.commonandroidmvvm.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.res.Configuration
import android.net.Uri
import android.text.format.DateFormat
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import com.viked.commonandroidmvvm.BaseApp
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.text.TextWrapper
import kotlinx.coroutines.*
import java.util.*


/**
 * Created by yevgeniishein on 7/25/17.
 */
fun Double.cutDecimal(count: Int) = String.format(Locale.ROOT, "%.${count}f", this)

fun Context.openLink(link: String) {
    val i = Intent(ACTION_VIEW)
    i.data = Uri.parse(link)
    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    when {
        checkIntent(i) -> startActivity(i)
        addTextToClipboard(TextWrapper(R.string.app_name), TextWrapper(link)) -> showToast(R.string.intent_open_link_error)
        else -> showToast(R.string.error)
    }
}

fun Context.sendEmail(email: String, subject: String? = null) {
    val uri = Uri.fromParts("mailto", email, null).buildUpon().apply {
        if (subject != null) {
            appendQueryParameter("subject", subject)
        }
    }.build()

    val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
    val intent = Intent.createChooser(emailIntent, getString(R.string.send_email))
    when {
        checkIntent(intent) -> startActivity(intent)
        else -> showToast(R.string.error)
    }
}

fun Context.makeCall(phone: String) {
    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
    dialIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    val intent = Intent.createChooser(dialIntent, getString(R.string.call_with))
    when {
        checkIntent(intent) -> startActivity(intent)
        else -> showToast(R.string.error)
    }
}

fun Context.shareApp() {
    val shareBody = "${getString(R.string.app_name)} ${getAppUrl()}"
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
    val intent = Intent.createChooser(sharingIntent, getString(R.string.share_with))
    when {
        checkIntent(intent) -> startActivity(intent)
        addTextToClipboard(TextWrapper(R.string.app_name), TextWrapper(getAppUrl())) -> showToast(R.string.intent_open_link_error)
        else -> showToast(R.string.error)
    }
}

fun Context.rateApp() {
    val playIntent = Intent(ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
    val urlIntent = Intent(ACTION_VIEW, Uri.parse(getAppUrl()))
    when {
        checkIntent(playIntent) -> startActivity(playIntent)
        checkIntent(urlIntent) -> startActivity(urlIntent)
        addTextToClipboard(TextWrapper(R.string.app_name), TextWrapper(getAppUrl())) -> showToast(R.string.intent_open_link_error)
        else -> showToast(R.string.error)
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

fun Context.checkIntent(intent: Intent): Boolean = intent.resolveActivity(packageManager) != null
fun Context.showToast(@StringRes stringId: Int) {
    Toast.makeText(this,
            getString(stringId),
            Toast.LENGTH_LONG)
            .show()
}

fun Context.runIntent(intent: Intent, @StringRes errorStringId: Int) {
    if (checkIntent(intent)) {
        startActivity(intent)
    } else {
        showToast(errorStringId)
    }
}

fun Context.addTextToClipboard(label: TextWrapper, text: TextWrapper): Boolean {
    return try {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label[this], text[this])
        clipboard.setPrimaryClip(clip)
        true
    } catch (e: Exception) {
        e.log()
        false
    }
}

fun Context.isInDarkMode() = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
