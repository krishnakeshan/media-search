package com.example.mediasearch.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MediaDAO {
    @Query("SELECT * FROM Media")
    suspend fun getAll(): List<Media>

    @Query("SELECT * FROM media WHERE tags LIKE '%' || :tag || '%'")
    suspend fun findByTag(tag: String): List<Media>

    @Insert
    suspend fun insert(media: Media)
}