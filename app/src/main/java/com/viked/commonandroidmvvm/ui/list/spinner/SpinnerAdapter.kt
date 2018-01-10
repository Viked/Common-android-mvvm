package com.viked.commonandroidmvvm.ui.list.spinner

import android.databinding.ObservableField
import android.databinding.ObservableList
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.list.ItemWrapper

/**
 * Created by shein on 1/10/2018.
 */
class SpinnerAdapter(private val spinner: Spinner,
                     private val list: ObservableList<ItemWrapper>,
                     private val selected: ObservableField<String>,
                     private val placeHolder: TextWrapper = TextWrapper(""),
                     private val content: MutableList<String> = mutableListOf()) :
        ArrayAdapter<String>(spinner.context, android.R.layout.simple_spinner_item, content), AdapterView.OnItemSelectedListener {

    fun onAttach() {
        spinner.adapter = this
        updateSelection()
        spinner.onItemSelectedListener = this
        list.addOnListChangedCallback(onListChangeCallback)
    }

    fun onDetach() {
        spinner.adapter = null
        spinner.onItemSelectedListener = null
        list.removeOnListChangedCallback(onListChangeCallback)
    }

    override fun notifyDataSetChanged() {
        content.clear()
        content.addAll(list.map { it.name })
        content.add(placeHolder[spinner.context])
        super.notifyDataSetChanged()
        updateSelection()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Not implemented
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        if (position < list.size) {
            selected.set(list[position].name)
        } else {
            selected.set("")
        }
    }

    private fun updateSelection() {
        val selected = selected.get()
        if (selected.isEmpty() || list.map { it.name }.find { it == selected } == null) {
            this.selected.set("")
            spinner.setSelection(count)
        }
    }

    private val onListChangeCallback: ObservableList.OnListChangedCallback<ObservableList<ItemWrapper>> =
            object : ObservableList.OnListChangedCallback<ObservableList<ItemWrapper>>() {
                override fun onItemRangeRemoved(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeMoved(sender: ObservableList<ItemWrapper>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeChanged(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onChanged(sender: ObservableList<ItemWrapper>?) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeInserted(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }
            }

}