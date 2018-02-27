package com.viked.commonandroidmvvm.ui.binding

import android.app.Activity
import android.databinding.BindingAdapter
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.common.HideKeyoardClickListener
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import kotlin.math.roundToInt


/**
 * Created by yevgeniishein on 10/12/17.
 */

@BindingAdapter("visibleGone")
fun setVisible(view: View, show: Boolean?) {
    view.visibility = if (show != false) View.VISIBLE else View.GONE
}

@BindingAdapter("fabContext")
fun setFabContext(fab: FloatingActionButton, fabBindingContext: FabBindingContext?) {
    if (fabBindingContext != null && fabBindingContext.iconId > 0) {
        fab.setImageResource(fabBindingContext.iconId)
        fab.setOnClickListener(HideKeyoardClickListener { fabBindingContext.onClick() })
        fab.visibility = View.VISIBLE
        fab.show()
    } else {
        fab.setOnClickListener(null)
        fab.visibility = View.GONE
        fab.hide()
    }
}

@BindingAdapter("ptrListener")
fun setPtrListener(layout: SwipeRefreshLayout, listener: SwipeRefreshLayout.OnRefreshListener?) {
    layout.setOnRefreshListener(listener)
}

@BindingAdapter("ptrLoading")
fun setPtrLoading(layout: SwipeRefreshLayout, loading: Boolean?) {
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

@BindingAdapter("adapter")
fun setAdapter(spinner: Spinner, list: List<ItemWrapper>?) {
    spinner.adapter = ArrayAdapter(
            spinner.context,
            android.R.layout.simple_spinner_item,
            list?.map { it.name }?.toTypedArray() ?: arrayOf())
}

@BindingAdapter("view")
fun setView(container: ViewGroup, view: View?) {
    container.removeAllViews()
    view?.run { container.addView(this) }
}