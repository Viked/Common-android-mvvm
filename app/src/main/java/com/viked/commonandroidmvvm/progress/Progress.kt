package com.viked.commonandroidmvvm.progress

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Progress(@PrimaryKey(autoGenerate = false) val id: String, val value: Int)