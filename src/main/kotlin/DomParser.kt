class DomParser(private val name: String) {
    fun find(source: String): Element? {
        val prefix = "<$name"
        val suffix = "</$name>"
        val start = source.indexOf(prefix)
        val tagEnd = source.indexOf(">")
        val end = source.indexOf(suffix)

        when {
            start != -1 && end == -1 -> throw Exception("$name has no proper end!")
            (start == -1 || end < start) -> return null
        }
        val tag = source.substring(start + prefix.length, tagEnd)
        val attributes = determineAttributes(tag)
        val content = source.substring(tagEnd + 1, end)

        return Element(start, end + suffix.length, attributes, content)
    }

    private fun determineAttributes(tag: String): Map<String, String> {
        return tag.trim().split(" ").associate { keyPair ->
            val parts = keyPair.split("=")
            val key = parts.first().trim()
            val value = parts.last().replace("\"", "")
            key to value
        }
    }

    private fun getBetween(prefix: String, suffix: String, source: String): String? {
        val start = source.lastIndexOf(prefix)
        val end = source.lastIndexOf(suffix)
        return if (start == -1 || end == -1 || end < start) {
            null
        } else {
            source.substring(start + prefix.length, end)
        }
    }
}