package com.phonecluster.app.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: FileEntity)

    @Query("SELECT * FROM files")
    suspend fun getAllFiles(): List<FileEntity>

    @Query("DELETE FROM files WHERE serverFileId = :serverFileId")
    suspend fun deleteByServerId(serverFileId: Int)
}
