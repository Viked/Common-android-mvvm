package com.viked.commonandroidmvvm.progress

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface ProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun set(value: Progress)

    @Query("SELECT * FROM Progress WHERE id = :id")
    operator fun get(id: String): LiveData<Progress>

    @Delete
    fun delete(value: Progress)

}