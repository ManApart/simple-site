data class Context(
    val data: Map<String, Any>,
    val files: Map<String, String> = mapOf(),
    val scopedData: Map<String, Any> = mapOf<String, String>()
) {
    fun getValue(keyPath: String): Any? {
        return getValue(keyPath.split("."))
    }

    fun getValue(parts: List<String>): Any? {
        return data.getNestedValue(parts)
            ?: scopedData.getNestedValue(parts)
    }
}

private fun <K, V> Map<K, V>.getNestedValue(keys: List<String>): Any? {
    return if (keys.size == 1) {
        getByName(keys.first())
    } else {
        val newMap = getByName(keys.first())
        if (newMap is List<*>) {
            throw IllegalStateException("Can't handle arrays: $newMap")
        } else if (newMap == null) {
            return null
        }
        (newMap as Map<String, Any>).getNestedValue(keys.subList(1, keys.size))
    }
}

private fun <K, V> Map<K, V>.getByName(name: String): V? {
    return entries.firstOrNull { it.key == name }?.value
//    return entries.firstOrNull { it.key == name }?.value value?: throw IllegalArgumentException("No value for $name")
}