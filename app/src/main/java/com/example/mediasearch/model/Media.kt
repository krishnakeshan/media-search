package com.example.mediasearch.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Media(
    @PrimaryKey(autoGenerate = true) val id: Integer? = null,
    val type: MediaType,
    val url: String,
    val tags: String
) {
    enum class MediaType {
        IMAGE, VIDEO, AUDIO
    }
}