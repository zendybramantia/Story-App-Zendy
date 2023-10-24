package com.zendy.storyapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Query("SELECT * FROM stories")
    fun getAllStory(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}