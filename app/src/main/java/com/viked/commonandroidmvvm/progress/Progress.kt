package com.viked.commonandroidmvvm.progress

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.StringRes
import com.viked.commonandroidmvvm.text.TextWrapper

@Entity
data class Progress(@PrimaryKey(autoGenerate = false) val id: String, val value: Int, @StringRes val messageId: Int) {

    fun getMessage() = TextWrapper { it.getString(messageId, value) }

}