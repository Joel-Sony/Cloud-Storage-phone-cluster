package com.phonecluster.app.search

import kotlin.math.ln

class BM25Ranker(
    private val k1: Double = 1.5,
    private val b: Double = 0.75
) {

    fun score(
        queryTf: Map<Int, Int>,
        docTf: Map<Int, Int>,
        docLength: Int,
        avgDocLength: Double,
        docFreq: Map<Int, Int>,
        totalDocs: Int
    ): Double {

        var score = 0.0

        for ((term, _) in queryTf) {
            val tf = docTf[term] ?: continue
            val df = docFreq[term] ?: continue

            val idf = ln((totalDocs - df + 0.5) / (df + 0.5) + 1)

            val numerator = tf * (k1 + 1)
            val denominator =
                tf + k1 * (1 - b + b * (docLength / avgDocLength))

            score += idf * (numerator / denominator)
            println(
                "BM25 TRACE → term=$term tf=$tf df=$df idf=$idf docLen=$docLength avgdl=$avgDocLength"
            )

        }

        return score
    }
}
