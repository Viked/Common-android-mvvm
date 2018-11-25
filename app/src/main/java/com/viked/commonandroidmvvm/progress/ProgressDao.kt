package com.viked.commonandroidmvvm.progress

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface ProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun set(value: Progress)

    @Query("SELECT * FROM Progress WHERE id IN (:ids)")
    operator fun get(ids: List<String>): LiveData<List<Progress>>

    @Delete
    fun delete(value: Progress)

}