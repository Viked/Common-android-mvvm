package com.viked.commonandroidmvvm.ui.binding

import android.app.Activity
import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.widget.Toolbar
import android.text.SpannableString
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.viked.commonandroidmvvm.drawable.AvatarDrawable
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.utils.getAndroidDrawable
import java.util.*
import kotlin.math.roundToInt


/**
 * Created by yevgeniishein on 10/12/17.
 */

@BindingAdapter("visibleGone")
fun setVisible(view: View, show: Boolean?) {
    view.visibility = if (show != false) View.VISIBLE else View.GONE
}

@BindingAdapter("ptrListener")
fun setPtrListener(layout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout, listener: androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener?) {
    layout.setOnRefreshListener(listener)
}

@BindingAdapter("ptrLoading")
fun setPtrLoading(layout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout, loading: Boolean?) {
    layout.isRefreshing = loading != null && loading
}

@BindingAdapter("title")
fun setTitle(layout: Toolbar, title: TextWrapper?) {
    val context = layout.context
    if (context is Activity) {
        context.title = title?.get(layout.context) ?: ""
    }
}

@BindingAdapter("textUnderline")
fun setUnderlinedText(textView: TextView, stringId: Int) {
    val text = textView.context.getString(stringId) ?: ""
    setUnderlinedText(textView, text)
}

@BindingAdapter("textUnderline")
fun setUnderlinedText(textView: TextView, string: String?) {
    val spannableText = SpannableString(string ?: "")
    spannableText.setSpan(UnderlineSpan(), 0, spannableText.length, 0)
    textView.text = spannableText
}

@BindingAdapter("percentWidth")
fun setPercentWidth(view: View, percent: Float) {
    val metrics = DisplayMetrics()
    (view.context as Activity).windowManager.defaultDisplay.getMetrics(metrics)
    view.layoutParams.width = (metrics.widthPixels.toFloat() * percent).roundToInt()
    view.requestLayout()
}

@BindingAdapter("imageRes")
fun setImageRes(view: ImageView, imageResId: Int?) {
    view.setImageResource(imageResId ?: 0)
}

@BindingAdapter("editable")
fun setSelected(editText: EditText, editable: Boolean?) {
    val value = editable != null && editable
    editText.isEnabled = value
}

@BindingAdapter("imageUrl")
fun setImageView(imageView: ImageView, url: String) {
    // Glide.with(imageView).load(url).into(imageView)
}

@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resource: Int?) {
    imageView.setImageResource(resource ?: 0)
}

@BindingAdapter("adapter", "android:selectedItemPosition", requireAll = false)
fun setAdapter(spinner: Spinner, list: List<ItemWrapper>?, selectedItemPosition: Int?) {
    spinner.adapter = ArrayAdapter(
            spinner.context,
            android.R.layout.simple_spinner_item,
            list?.map { it.name[spinner.context] }?.toTypedArray() ?: arrayOf())
    if (selectedItemPosition != null && list != null && list.size > selectedItemPosition) {
        spinner.setSelection(selectedItemPosition)
    }
}

@BindingAdapter("view")
fun setView(container: ViewGroup, view: View?) {
    container.removeAllViews()
    view?.run { container.addView(this) }
}

@BindingAdapter("android:text")
fun setText(container: TextView, text: TextWrapper?) {
    container.text = text?.get(container.context)
}

@BindingAdapter("collapsing")
fun setCollapsingPeriod(appBarLayout: AppBarLayout, any: Any?) {
    appBarLayout.setExpanded(any != null)
}

@BindingAdapter("collapsingValue")
fun setCollapsingValue(appBarLayout: AppBarLayout, any: Boolean?) {
    appBarLayout.setExpanded(any == true)
}

@BindingAdapter("date")
fun setDate(textView: TextView, date: Date?) {
    var text = ""
    if (date != null) {
        text = DateFormat.getDateFormat(textView.context).format(date)
    }
    textView.text = text
}

@BindingAdapter("date")
fun setDate(textView: TextView, date: Long?) {
    var text = ""
    if (date != null) {
        text = DateFormat.getDateFormat(textView.context).format(Date(date))
    }
    textView.text = text
}

@BindingAdapter("fullDate")
fun setFullDate(textView: TextView, date: Date?) {
    var text = ""
    if (date != null) {
        text = DateFormat.getDateFormat(textView.context).format(date) + " - " + DateFormat.getTimeFormat(textView.context).format(date)
    }
    textView.text = text
}

@BindingAdapter("fullDate")
fun setFullDate(textView: TextView, date: Long?) {
    var text = ""
    if (date != null) {
        text = DateFormat.getDateFormat(textView.context).format(Date(date)) + " - " + DateFormat.getTimeFormat(textView.context).format(Date(date))
    }
    textView.text = text
}

@BindingAdapter("commentIcon")
fun setCommentIcon(view: ImageView, comment: String) {
    view.visibility = if (TextUtils.isEmpty(comment)) View.GONE else View.VISIBLE
}

@BindingAdapter("autoCompleteAdapter")
fun setAdapter(textView: MultiAutoCompleteTextView, list: List<String>?) {
    textView.setAdapter(ArrayAdapter(
            textView.context,
            android.R.layout.simple_spinner_item,
            list ?: listOf()))
    textView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
    textView.clearFocus()
}

@BindingAdapter("time")
fun setTime(textView: TextView, date: Long?) {
    var text = ""
    if (date != null) {
        text = DateFormat.getTimeFormat(textView.context).format(Date(date))
    }
    textView.text = text
}

@BindingAdapter("android:drawableLeft")
fun setDrawableLeft(view: TextView, resourceId: Int?) {
    resourceId ?: return
    val drawable = ContextCompat.getDrawable(view.context, resourceId) ?: return
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val drawables = view.compoundDrawables
    val padding = view.compoundDrawablePadding
    view.setCompoundDrawables(drawable, drawables[1], drawables[2], drawables[3])
    view.compoundDrawablePadding = padding
}

@BindingAdapter("avatar")
fun setAvatar(imageView: ImageView, text: String?) {
    imageView.setImageDrawable(AvatarDrawable(text ?: ""))
}

@BindingAdapter("android:button")
fun setAvatar(compoundButton: CompoundButton, resourceId: Int?) {
    if (resourceId == null || resourceId == 0) return
    val drawableResourceId = compoundButton.context.getAndroidDrawable(resourceId)
    compoundButton.setButtonDrawable(drawableResourceId)
}

@BindingAdapter("errorText")
fun setErrorText(view: TextInputLayout, errorMessage: Int?) {
    view.error = if (errorMessage != null && errorMessage != 0) {
        view.context.getString(errorMessage)
    } else null
}