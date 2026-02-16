package com.phonecluster.app.vectorization

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.math.ln

class TFIDFVectorizer {

    // term -> index
    private val vocabulary = mutableMapOf<String, Int>()

    // index -> document frequency
    private val docFreq = mutableMapOf<Int, Int>()

    // total documents indexed
    private var docCount = 0

    /**
     * Call ONLY during indexing
     * Updates vocabulary + document frequency
     */
    fun fitDocument(tokens: List<String>) {
        docCount++
        tokens.toSet().forEach { term ->
            val index = vocabulary.getOrPut(term) { vocabulary.size }
            docFreq[index] = docFreq.getOrDefault(index, 0) + 1
        }
    }

    /**
     * TF-IDF vector (for cosine similarity, NOT BM25)
     */
    fun transform(tokens: List<String>): SparseVector {
        val tf = mutableMapOf<Int, Double>()

        tokens.forEach { term ->
            val index = vocabulary[term] ?: return@forEach
            tf[index] = tf.getOrDefault(index, 0.0) + 1.0
        }

        val values = tf.mapValues { (idx, freq) ->
            val df = docFreq[idx] ?: 1
            val idf = ln((docCount + 1.0) / (df + 1.0))
            freq * idf
        }

        return SparseVector(values)
    }

    /**
     * RAW term frequency (for BM25)
     * DOES NOT fit or mutate vocab
     */
    fun termFrequency(tokens: List<String>): Map<Int, Int> {
        val tf = mutableMapOf<Int, Int>()

        tokens.forEach { term ->
            val index = vocabulary[term] ?: return@forEach
            tf[index] = tf.getOrDefault(index, 0) + 1
        }

        return tf
    }

    /**
     * Serialize raw TF map → ByteArray (Room-safe)
     */
    fun serializeTermFreq(tf: Map<Int, Int>): ByteArray {
        val buffer = ByteArrayOutputStream()
        val out = DataOutputStream(buffer)

        out.writeInt(tf.size)
        for ((term, freq) in tf) {
            out.writeInt(term)
            out.writeInt(freq)
        }

        return buffer.toByteArray()
    }

    /**
     * Deserialize ByteArray → raw TF map
     */
    fun deserializeTermFreq(bytes: ByteArray): Map<Int, Int> {
        val tf = mutableMapOf<Int, Int>()
        val input = DataInputStream(ByteArrayInputStream(bytes))

        val size = input.readInt()
        repeat(size) {
            val term = input.readInt()
            val freq = input.readInt()
            tf[term] = freq
        }

        return tf
    }

    // ---- BM25 SUPPORT ----

    fun getDocFreq(): Map<Int, Int> = docFreq

    fun getDocCount(): Int = docCount
}
