package websiteBuilder.directives


class Interpolation(
    val start: Int,
    val end: Int,
    val keyPath: String
) {

    fun compute(source: String, data: Map<String, Any>): String {
        val parts = keyPath.split(".")
        val newValue: String = data.getNestedValue(parts) as String
        return source.substring(0, start) + newValue + source.substring(end, source.length)
    }

    private fun <K, V> Map<K, V>.getNestedValue(keys: List<String>): Any {
        return if (keys.size == 1){
            getByName(keys.first()).toString()
        } else {
            val newMap = getByName(keys.first())
            if (newMap is List<*>){
                throw IllegalStateException("Can't handle arrays: $newMap")
            }
            (newMap as Map<String, Any>).getNestedValue(keys.subList(1, keys.size))
        }
    }

    private fun <K, V> Map<K, V>.getByName(name: String): V {
        return entries.firstOrNull { it.key == name }?.value ?: throw IllegalArgumentException("No value for $name")
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