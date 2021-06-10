package directives

import getNestedValue

class Interpolation(
    val start: Int,
    val end: Int,
    val keyPath: String
) {

    fun compute(
        source: String,
        data: Map<String, Any>,
        scopedData: Map<String, Any> = mapOf<String, String>()
    ): String {
        val parts = keyPath.split(".")
        val newValue = getValue(data, parts, scopedData)
        return if (newValue != null) {
            source.substring(0, start) + newValue + source.substring(end, source.length)
        } else {
            source
        }
    }

    private fun getValue(
        data: Map<String, Any>,
        parts: List<String>,
        scopedData: Map<String, Any>
    ): String? {
        return data.getNestedValue(parts)?.toString()
            ?: scopedData.getNestedValue(parts)?.toString()
    }

    companion object {
        fun find(source: String, searchStart: Int = 0): Interpolation? {
            val prefix = "{{"
            val suffix = "}}"
            val start = source.indexOf(prefix, searchStart)
            val end = source.indexOf(suffix, searchStart)
            return when {
                start != -1 && end == -1 -> throw Exception("Include has no proper end!")
                (start == -1 || end <= start) -> null
                else -> {
                    val inner = source.substring(start + prefix.length, end)
                    if (inner.isBlank()) {
                        null
                    } else {
                        Interpolation(start, end + suffix.length, inner)
                    }
                }
            }
        }
    }
}