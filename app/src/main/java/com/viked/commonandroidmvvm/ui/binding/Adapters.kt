package com.viked.commonandroidmvvm.ui.binding

import android.app.Activity
import android.databinding.BindingAdapter
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.common.FabBindingContext
import com.viked.commonandroidmvvm.ui.common.HideKeyoardClickListener

/**
 * Created by yevgeniishein on 10/12/17.
 */
object Adapters {

    @BindingAdapter("visibleGone")
    @JvmStatic
    fun setVisible(view: View, show: Boolean?) {
        view.visibility = if (show != false) View.VISIBLE else View.GONE
    }

    @BindingAdapter("fabContext")
    @JvmStatic
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

    @BindingAdapter("view")
    @JvmStatic
    fun setView(container: ViewGroup, view: View?) {
        container.removeAllViews()
        view?.run { container.addView(this) }
    }

    @BindingAdapter("ptrListener")
    @JvmStatic
    fun setPtrListener(layout: SwipeRefreshLayout, listener: SwipeRefreshLayout.OnRefreshListener?) {
        layout.setOnRefreshListener(listener)
    }

    @BindingAdapter("ptrLoading")
    @JvmStatic
    fun setPtrLoading(layout: SwipeRefreshLayout, loading: Boolean?) {
        layout.isRefreshing = loading != null && loading
    }

    @BindingAdapter("title")
    @JvmStatic
    fun setPtrLoading(layout: Toolbar, title: TextWrapper?) {
        val context = layout.context
        if (context is Activity) {
            context.title = title?.get(layout.context) ?: ""
        }
    }

}