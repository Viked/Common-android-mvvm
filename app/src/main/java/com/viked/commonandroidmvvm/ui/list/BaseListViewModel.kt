package com.viked.commonandroidmvvm.ui.list

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.support.v4.widget.SwipeRefreshLayout
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.rx.SubscriptionBuilder
import com.viked.commonandroidmvvm.ui.common.BaseViewModel

/**
 * Created by yevgeniishein on 10/15/17.
 */
abstract class BaseListViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener {

    private val listLoadKey = 1
    private val listRemoveKey = 2

    open val emptyListMessageId = R.string.empty_list

    val list: ObservableList<ItemWrapper> = ObservableArrayList<ItemWrapper>()

    var empty: ObservableListEmpty = ObservableListEmpty(list)

    abstract fun getListSubscriptionBuilder(): SubscriptionBuilder<List<ItemWrapper>>

    abstract fun removeItemsSubscriptionBuilder(list: List<ItemWrapper>): SubscriptionBuilder<Any>

    open fun canRemove(wrapper: ItemWrapper) = wrapper.selectable && wrapper.selected.get()

    override fun onCleared() {
        empty.clear()
        super.onCleared()
    }

    fun load() {
        subscribe({ getListSubscriptionBuilder().addListSubscription() }, listLoadKey)
    }

    open fun SubscriptionBuilder<List<ItemWrapper>>.addListSubscription() =
            addOnSubscribe { list.clear() }
                    .addOnError { list.clear() }
                    .addOnNext { list.addAll(it) }

    fun removeSelected() {
        subscribe({ removeItemsSubscriptionBuilder(list.filter { canRemove(it) }) }, listRemoveKey)
    }

    override fun onRefresh() {
        load()
    }
}