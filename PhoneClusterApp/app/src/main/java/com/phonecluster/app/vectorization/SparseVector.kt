package com.phonecluster.app.vectorization
import java.nio.ByteBuffer

class SparseVector(
    val values: Map<Int, Double>
) {

    fun serialize(): ByteArray {
        val buffer = ByteBuffer.allocate(4 + values.size * 16)
        buffer.putInt(values.size)

        for ((key, value) in values) {
            buffer.putInt(key)
            buffer.putDouble(value)
        }

        return buffer.array()
    }

    companion object {
        fun deserialize(bytes: ByteArray): SparseVector {
            val buffer = ByteBuffer.wrap(bytes)
            val size = buffer.int
            val map = mutableMapOf<Int, Double>()

            repeat(size) {
                val key = buffer.int
                val value = buffer.double
                map[key] = value
            }

            return SparseVector(map)
        }
    }
}
