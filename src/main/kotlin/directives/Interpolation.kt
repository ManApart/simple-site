package directives

import getNestedValue

class Interpolation(
    val start: Int,
    val end: Int,
    val keyPath: String
) {

    fun compute(source: String, data: Map<String, Any>): String {
        val parts = keyPath.split(".")
        val newValue: String = data.getNestedValue(parts).toString()
        return source.substring(0, start) + newValue + source.substring(end, source.length)
    }



    companion object {
        fun find(source: String): Interpolation? {
            val prefix = "{{"
            val suffix = "}}"
            val start = source.indexOf(prefix)
            val end = source.indexOf(suffix)
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