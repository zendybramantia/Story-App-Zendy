package com.zendy.storyapp.data.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String
): Parcelable