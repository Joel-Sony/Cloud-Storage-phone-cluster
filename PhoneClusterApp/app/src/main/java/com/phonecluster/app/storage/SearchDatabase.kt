package com.phonecluster.app.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.phonecluster.app.search.DocumentEntity

@Database(
    entities = [DocumentEntity::class, VocabularyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SearchDatabase : RoomDatabase() {

    abstract fun documentDao(): DocumentDao

    companion   object {
        @Volatile
        private var INSTANCE: SearchDatabase? = null

        fun getInstance(context: Context): SearchDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SearchDatabase::class.java,
                    "search_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
