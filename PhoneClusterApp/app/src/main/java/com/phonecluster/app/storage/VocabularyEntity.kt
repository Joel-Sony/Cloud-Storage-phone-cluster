package com.phonecluster.app.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey
    val word: String,

    val idfScore: Double
)
