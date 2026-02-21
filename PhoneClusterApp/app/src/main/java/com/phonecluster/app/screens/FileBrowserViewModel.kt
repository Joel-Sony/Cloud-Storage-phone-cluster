package com.phonecluster.app.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.phonecluster.app.storage.AppDatabase
import com.phonecluster.app.storage.FileEntity

class FileBrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).fileDao()

    private val _files = MutableStateFlow<List<FileEntity>>(emptyList())
    val files: StateFlow<List<FileEntity>> = _files

    init {
        loadFiles()
    }

    private fun loadFiles() {
        viewModelScope.launch {
            _files.value = dao.getAllFiles()
        }
    }
}