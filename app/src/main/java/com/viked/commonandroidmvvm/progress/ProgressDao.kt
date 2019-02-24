package com.viked.commonandroidmvvm.progress

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun set(value: Progress)

    @Query("SELECT * FROM Progress WHERE id IN (:ids)")
    operator fun get(ids: List<String>): LiveData<List<Progress>>

    @Delete
    fun delete(value: Progress)

    @Query("DELETE FROM Progress")
    fun clean()

}