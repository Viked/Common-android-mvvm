package com.viked.commonandroidmvvm.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.text.format.DateFormat
import com.viked.commonandroidmvvm.R
import java.util.*


/**
 * Created by yevgeniishein on 7/25/17.
 */
fun Double.cutDecimal(count: Int) = String.format("%.${count}f", this)

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

fun Date.formatDate(context: Context): String {
    return DateFormat.getDateFormat(context).format(this)

}

fun Date.formatTime(context: Context): String {
    return DateFormat.getTimeFormat(context).format(this)
}