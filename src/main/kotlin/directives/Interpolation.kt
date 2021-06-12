package directives

import Context

class Interpolation(
    val start: Int,
    val end: Int,
    val keyPath: String
) {

    fun compute(
        source: String,
        context: Context
    ): String {
        val parts = keyPath.split(".")
        val newValue = context.getValue(parts)?.toString()
        return if (newValue != null) {
            source.substring(0, start) + newValue + source.substring(end, source.length)
        } else {
            source
        }
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

fun String.interpolate(context: Context): String {
    var interpolated = this
    var directive = Interpolation.find(interpolated)
    while (directive != null) {
        interpolated = directive.compute(interpolated, context)
        directive = Interpolation.find(interpolated, directive.end)
    }

    return interpolated
}