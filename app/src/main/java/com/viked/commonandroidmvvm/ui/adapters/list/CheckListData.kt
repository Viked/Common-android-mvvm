package com.viked.commonandroidmvvm.ui.adapters.list

interface CheckListData {

    val id: Long

    val checked: Boolean

    fun getTitle(): String

    fun getSubtitle(): String

}