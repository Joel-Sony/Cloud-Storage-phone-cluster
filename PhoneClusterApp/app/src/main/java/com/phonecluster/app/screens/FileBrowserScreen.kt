package com.phonecluster.app.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.phonecluster.app.storage.FileEntity

@Composable
fun FileBrowserScreen(
    onBackClick: () -> Unit,
    viewModel: FileBrowserViewModel = viewModel()
) {
    val files by viewModel.files.collectAsState()

    Column {

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Back")
        }

        LazyColumn {
            items(files) { file ->
                FileItem(file)
            }
        }
    }
}

@Composable
fun FileItem(file: FileEntity) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Text(text = file.fileName)
        Text(text = "Size: ${file.fileSize} bytes")

        Row {
            Button(onClick = { /* TODO: Download */ }) {
                Text("Download")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { /* TODO: Delete */ }) {
                Text("Delete")
            }
        }
    }
}