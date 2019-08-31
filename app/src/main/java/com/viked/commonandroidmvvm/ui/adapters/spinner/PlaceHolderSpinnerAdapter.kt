package com.viked.commonandroidmvvm.ui.adapters.spinner

import android.database.DataSetObserver
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.list.emptyTextWrapper

/**
 * Created by shein on 1/10/2018.
 */
class PlaceHolderSpinnerAdapter(private val spinner: Spinner,
                                private val data: ObservableField<SpinnerData>,
                                private val placeHolder: TextWrapper = TextWrapper("")) : SpinnerAdapter, AdapterDelegate, ListAdapter, AdapterView.OnItemSelectedListener {

    private val EXTRA = 1

    private val content: MutableList<String> = mutableListOf()
    private val adapter = ArrayAdapter<String>(spinner.context, android.R.layout.simple_spinner_item, content)

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Not implemented
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        data.get()?.apply {
            selected = if (position != 0) {
                list[position - EXTRA]
            } else {
                emptyTextWrapper
            }
        }
        data.notifyChange()
    }

    override fun isEmpty() = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // This provides the View for the Selected Item in the Spinner, not
        // the dropdown (unless dropdownView is not set).
        if (position == 0) {
            return getNothingSelectedView(parent)
        }
        return adapter.getView(position - EXTRA, null, parent)
        // the convertView if possible.

    }

    override fun registerDataSetObserver(observer: DataSetObserver?) = adapter.registerDataSetObserver(observer)

    override fun getItemViewType(position: Int) = 0

    override fun getItem(position: Int) =
            if (position == 0) null else adapter.getItem(position - EXTRA)

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) =
            if (position >= EXTRA) adapter.getItemId(position - EXTRA) else position.toLong() - EXTRA.toLong()

    override fun hasStableIds() = adapter.hasStableIds()

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Android BUG! http://code.google.com/p/android/issues/detail?id=17128 -
        // Spinner does not support multiple view types
        return if (position == 0) {
            View(spinner.context)
        } else {
            adapter.getDropDownView(position - EXTRA, null, parent)
        }
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        adapter.unregisterDataSetObserver(observer)
    }

    override fun getCount() = EXTRA + adapter.count

    override fun isEnabled(position: Int): Boolean = position != 0

    override fun areAllItemsEnabled() = false

    private fun getNothingSelectedView(parent: ViewGroup?): View {
        return LayoutInflater.from(spinner.context)
                .inflate(android.R.layout.simple_spinner_item, parent, false)
                .apply { (this as TextView).text = placeHolder[spinner.context] }
    }

    private fun getSelectedIndex() =
            data.get()?.run {
                if (selected.name[spinner.context].isEmpty()) {
                    0
                } else {
                    list.indexOf(selected) + EXTRA
                }
            } ?: 0

    private val selectedChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            update()
        }
    }

    override fun subscribe() {
        spinner.adapter = this
        data.addOnPropertyChangedCallback(selectedChangedCallback)
        update()
    }

    override fun unsubscribe() {
        spinner.adapter = null
        spinner.onItemSelectedListener = null
        data.removeOnPropertyChangedCallback(selectedChangedCallback)
    }

    override fun update() {
        spinner.onItemSelectedListener = null
        content.clear()
        data.get()?.apply {
            content.addAll(list.map { it.name[spinner.context] })
            spinner.setSelection(getSelectedIndex())
        }

        spinner.onItemSelectedListener = this@PlaceHolderSpinnerAdapter
        adapter.notifyDataSetChanged()
    }
}