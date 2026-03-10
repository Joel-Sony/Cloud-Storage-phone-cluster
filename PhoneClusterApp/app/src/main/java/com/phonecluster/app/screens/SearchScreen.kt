package com.phonecluster.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.phonecluster.app.ml.EmbeddingEngine
import com.phonecluster.app.ml.OnnxTokenizer
import com.phonecluster.app.ml.SimilarityUtils
import com.phonecluster.app.storage.AppDatabase

private val BgDeep = Color(0xFF020617)
private val BgCard = Color(0xFF0D1424)
private val BorderSubtle = Color(0xFF1E293B)

private val AccentCyan = Color(0xFF22D3EE)
private val AccentPurple = Color(0xFFA78BFA)

private val TextPrimary = Color(0xFFF1F5F9)
private val TextMuted = Color(0xFF475569)

data class ChatMessage(
    val text: String? = null,
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
        containerColor = BgDeep,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Smart Search", color = TextPrimary)
                        Text(
                            "AI file discovery",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(messages) { message ->

                    message.text?.let {
                        QueryCard(it)
                    }

                    message.results?.forEach { result ->
                        Spacer(modifier = Modifier.height(13.dp))
                        FileResultCard(result)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgCard)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about your files...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = {

                        if (query.isBlank()) return@IconButton

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
                                dao.getAllFilesOnce()
                            }

                            val ranked = files.map {

                                val score =
                                    SimilarityUtils.cosineSimilarity(
                                        queryEmbedding,
                                        it.embedding
                                    )

                                it to score

                            }.sortedByDescending { it.second }
                                .take(3)

                            messages = messages + ChatMessage(text = query)

                            val resultList = ranked.map { (file, score) ->
                                SearchResult(
                                    fileName = file.fileName,
                                    fileType = file.fileType,
                                    score = score
                                )
                            }

                            messages = messages + ChatMessage(
                                text = "Top matches",
                                results = resultList
                            )

                            query = ""
                            isSearching = false
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = AccentCyan)
                }
            }
        }
    }
}

@Composable
fun QueryCard(query: String) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = AccentPurple,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = query,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun FileResultCard(result: SearchResult) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1A1030)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = Color(0xFFFC8181),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = result.fileName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Similarity: %.2f".format(result.score),
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = "Download",
                    tint = AccentCyan
                )
            }
        }
    }
}