package com.phonecluster.app.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phonecluster.app.search.DocumentEntity

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: DocumentEntity)

    @Query("SELECT * FROM documents")
    suspend fun getAll(): List<DocumentEntity>

    @Query("SELECT COUNT(*) FROM documents")
    suspend fun count(): Int
}
