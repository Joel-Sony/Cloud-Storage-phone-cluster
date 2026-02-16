package com.phonecluster.app.preprocessing

object TextPreprocessor {

    fun process(text: String): List<String> {
        return text
            .lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .split("\\s+".toRegex())
            .filter { it.length > 2 }
            .filterNot { Stopwords.words.contains(it) }
    }
}
