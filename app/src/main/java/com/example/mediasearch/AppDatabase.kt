package com.example.mediasearch

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mediasearch.model.Media
import com.example.mediasearch.model.MediaDAO

@Database(entities = [Media::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun mediaDAO(): MediaDAO
}