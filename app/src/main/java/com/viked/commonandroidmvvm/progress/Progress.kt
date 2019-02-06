package com.viked.commonandroidmvvm.progress

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.StringRes
import com.viked.commonandroidmvvm.text.TextWrapper

@Entity
data class Progress(@PrimaryKey(autoGenerate = false) val id: String, val value: Int, @StringRes val messageId: Int) {

    fun getMessage() = TextWrapper { it.getString(messageId, value) }

}