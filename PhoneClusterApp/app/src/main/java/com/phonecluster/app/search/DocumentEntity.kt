package com.phonecluster.app.search

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey
    val hash: String,
    val fileName: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val vector: ByteArray,
    val termCount: Int
)
