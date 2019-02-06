package com.viked.commonandroidmvvm.ui.adapters.radio

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import android.widget.RadioButton
import android.widget.RadioGroup
import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 2/24/18.
 */
class RadioGroupAdapter(private val radioGroup: RadioGroup, private val list: ObservableField<List<ItemWrapper>>) : AdapterDelegate, RadioGroup.OnCheckedChangeListener {

    private val onListChangeCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            update()
        }
    }

    override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
        list.get()
                ?.filter { it is RadioItemWrapper }
                ?.map { it as RadioItemWrapper }
                ?.forEach { it.selected.set(p1 == it.titleId) }
                ?.let {
                    list.removeOnPropertyChangedCallback(onListChangeCallback)
                    list.notifyChange()
                    list.addOnPropertyChangedCallback(onListChangeCallback)
                }
    }

    override fun subscribe() {
        radioGroup.setOnCheckedChangeListener(this)
        list.addOnPropertyChangedCallback(onListChangeCallback)
        update()
    }

    override fun unsubscribe() {
        radioGroup.setOnCheckedChangeListener(null)
        list.removeOnPropertyChangedCallback(onListChangeCallback)
    }

    override fun update() {
        radioGroup.setOnCheckedChangeListener(null)
        with(radioGroup) {
            removeAllViews()
            list.get()?.filter { it is RadioItemWrapper }
                    ?.map { it as RadioItemWrapper }
                    ?.forEach {
                        addView(RadioButton(context).apply {
                            text = it.name[context]
                            id = it.titleId
                            isChecked = it.selected.get()
                        })
                    }

        }
        radioGroup.setOnCheckedChangeListener(this)
    }

}