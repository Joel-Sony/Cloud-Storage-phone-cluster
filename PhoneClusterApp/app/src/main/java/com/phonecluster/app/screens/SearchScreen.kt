package com.phonecluster.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.phonecluster.app.ml.OnnxTokenizer
import com.phonecluster.app.ml.SimilarityUtils
import com.phonecluster.app.storage.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.phonecluster.app.ml.EmbeddingEngine
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

data class ChatMessage(
    val text: String? = null,
    val isUser: Boolean,
    val results: List<SearchResult>? = null
)

data class SearchResult(
    val fileName: String,
    val fileType: String,
    val score: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    engine: EmbeddingEngine,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    val db = AppDatabase.getDatabase(context)
    val dao = db.fileDao()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Semantic Search") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = false
            ) {
                items(messages) { message ->
                    if (message.text != null) {
                        ChatBubble(message)
                    }

                    message.results?.let { results ->
                        results.forEach { result ->
                            FileResultCard(result)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about your files...") }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (query.isBlank()) return@Button

                        scope.launch {
                            isSearching = true

                            val tokenizer = OnnxTokenizer(context)
                            val (inputIds, attentionMask, tokenTypeIds) =
                                tokenizer.tokenize(query)

                            val queryEmbedding = engine.generateEmbedding(
                                inputIds,
                                attentionMask,
                                tokenTypeIds
                            )

                            val files = withContext(Dispatchers.IO) {
                                dao.getAllFiles()
                            }

                            val ranked = files.map {
                                val score = SimilarityUtils.cosineSimilarity(
                                    queryEmbedding,
                                    it.embedding
                                )
                                it to score
                            }.sortedByDescending { it.second }
                                .take(3)

                            messages = messages + ChatMessage(query, true)

                            val resultList = ranked.map { (file, score) ->
                                SearchResult(
                                    fileName = file.fileName,
                                    fileType = file.fileType,
                                    score = score
                                )
                            }

                            messages = messages + ChatMessage(
                                text = "Here are the most relevant files:",
                                isUser = false,
                                results = resultList
                            )

                            query = ""
                            isSearching = false
                        }
                    }
                ) {
                    Text("Send")
                }
            }

        }
    }
}
@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser)
            Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (message.isUser)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .padding(6.dp)
                .widthIn(max = 280.dp)
        ) {
            message.text?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(12.dp),
                    color = if (message.isUser)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
@Composable
fun FileResultCard(result: SearchResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = result.fileName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Type: ${result.fileType}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Similarity: %.3f".format(result.score),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* TODO: Download logic later JOEL */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Download")
            }
        }
    }
}
