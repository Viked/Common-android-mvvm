package com.viked.commonandroidmvvm.ui.adapters.radio

import android.databinding.ObservableList
import android.widget.RadioButton
import android.widget.RadioGroup
import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.ListChangeCallback
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 2/24/18.
 */
class RadioGroupAdapter(private val radioGroup: RadioGroup, private val list: ObservableList<ItemWrapper>) : AdapterDelegate, RadioGroup.OnCheckedChangeListener {

    private val onListChangeCallback = ListChangeCallback(Runnable { update() })

    override fun onCheckedChanged(p0: RadioGroup?, p1: Int) = list
            .filter { it is RadioItemWrapper }
            .map { it as RadioItemWrapper }
            .forEach { it.selected.set(p1 == it.titleId) }


    override fun subscribe() {
        radioGroup.setOnCheckedChangeListener(this)
        list.addOnListChangedCallback(onListChangeCallback)
        update()
    }

    override fun unsubscribe() {
        radioGroup.setOnCheckedChangeListener(null)
        list.removeOnListChangedCallback(onListChangeCallback)
    }

    override fun update() = with(radioGroup) {
        removeAllViews()
        list.filter { it is RadioItemWrapper }
                .map { it as RadioItemWrapper }
                .forEach {
                    addView(RadioButton(context).apply {
                        text = it.name[context]
                        id = it.titleId
                        isSelected = it.selected.get()
                    })
                }
    }


}