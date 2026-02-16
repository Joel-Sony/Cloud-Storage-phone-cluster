package com.phonecluster.app.search

import android.content.Context
import android.net.Uri
import com.phonecluster.app.extraction.TextExtractorFactory
import com.phonecluster.app.preprocessing.TextPreprocessor
import com.phonecluster.app.utils.HashUtils
import com.phonecluster.app.vectorization.TFIDFVectorizer
import com.phonecluster.app.storage.SearchDatabase
import com.phonecluster.app.utils.FileUtils

object SearchEngine {

    // single shared vectorizer instance
    private val vectorizer = TFIDFVectorizer()
    private val ranker = BM25Ranker()

    suspend fun indexFile(context: Context, uri: Uri) {
        val extractor = TextExtractorFactory.getExtractor(context, uri)
        val rawText = extractor.extract(context, uri)
        val fileName = FileUtils.getFileName(context, uri)
        val tokens = TextPreprocessor.process(rawText)

        // update corpus stats
        vectorizer.fitDocument(tokens)

        val termFreq = vectorizer.termFrequency(tokens)

        val hash = HashUtils.sha256(rawText)
        val db = SearchDatabase.getInstance(context)

        db.documentDao().insert(
            DocumentEntity(
                hash = hash,
                fileName = fileName,
                vector = vectorizer.serializeTermFreq(termFreq),
                termCount = tokens.size
            )
        )

        println("Indexed file ${hash.take(8)} | tokens=${tokens.size}")
    }

    // 🔑 THIS IS THE IMPORTANT CHANGE
    suspend fun search(
        context: Context,
        query: String
    ): List<SearchResult> {

        val queryTokens = TextPreprocessor.process(query)
        val queryTf = vectorizer.termFrequency(queryTokens)

        if (queryTf.isEmpty()) return emptyList()

        val db = SearchDatabase.getInstance(context)
        val docs = db.documentDao().getAll()

        if (docs.isEmpty()) return emptyList()

        val avgDocLength =
            docs.map { it.termCount }.average().coerceAtLeast(1.0)

        val totalDocs = vectorizer.getDocCount()
        val docFreq = vectorizer.getDocFreq()

        val results = mutableListOf<SearchResult>()

        docs.forEach { doc ->
            val docTf = vectorizer.deserializeTermFreq(doc.vector)

            val score = ranker.score(
                queryTf = queryTf,
                docTf = docTf,
                docLength = doc.termCount,
                avgDocLength = avgDocLength,
                docFreq = docFreq,
                totalDocs = totalDocs
            )

            if (score > 0.0) {
                results.add(
                    SearchResult(
                        fileName = doc.fileName,
                        score = score
                    )
                )
            }
        }

        // return ranked results for UI
        return results.sortedByDescending { it.score }
    }
}
