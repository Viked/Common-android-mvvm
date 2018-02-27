package com.viked.commonandroidmvvm.ui.adapters.search

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.databinding.ObservableList
import android.support.v4.widget.ResourceCursorAdapter
import android.view.View
import android.widget.TextView
import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.ListChangeCallback
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 2/24/18.
 */
class SearchAdapter(private val context: Context, private var objectList: ObservableList<ItemWrapper>) :
        ResourceCursorAdapter(context, android.R.layout.simple_spinner_item, objectList.createCursor(context), 0), AdapterDelegate {

    private val onListChangeCallback = ListChangeCallback(Runnable { update() })

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val textView = view as TextView
        textView.text = cursor.getString(cursor.getColumnIndex(STRING_KEY))
    }

    override fun update() {
        changeCursor(objectList.createCursor(context))
    }

    override fun subscribe() {
        objectList.addOnListChangedCallback(onListChangeCallback)
        update()
    }

    override fun unsubscribe() {
        objectList.removeOnListChangedCallback(onListChangeCallback)
    }
}

private const val STRING_KEY = "string"
private const val ID_KEY = "_id"

private fun List<ItemWrapper>.createCursor(context: Context) = MatrixCursor(arrayOf(ID_KEY, STRING_KEY)).apply {
    forEachIndexed { index, itemWrapper -> addRow(arrayOf(index + 1, itemWrapper.name[context])) }
}
