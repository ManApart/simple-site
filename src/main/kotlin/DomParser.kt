class DomParser(private val name: String) {
    fun find(source: String): Element? {
        val prefix = "<$name"
        val suffix = "</$name>"
        val start = source.indexOf(prefix)
        val tagEnd = source.indexOf(">", start)
        val nextStart = source.indexOf(prefix, start + 1)
        val selfClosing = (getChar(source, tagEnd - 1) == '/' && (nextStart == -1 || nextStart > tagEnd))

        return if (selfClosing) {
            findSelfClosingElement(start, tagEnd, source, prefix)
        } else {
            findElement(start, tagEnd, source, prefix, suffix)
        }
    }

    private fun getChar(string: String, position: Int): Char? {
        return if (position >= 0 && position < string.length) string[position] else null
    }

    private fun findSelfClosingElement(
        start: Int,
        end: Int,
        source: String,
        prefix: String,
    ): Element? {
        when {
            start != -1 && end == -1 -> throw Exception("$name has no proper end!")
            (start == -1 || end < start) -> return null
        }
        val tag = source.substring(start + prefix.length, end - 1)
        val attributes = determineAttributes(tag)

        return Element(start, end + 1, attributes, "")
    }

    private fun determineAttributes(tag: String): Map<String, String> {
        return tag.trim().split(" ").associate { keyPair ->
            val parts = keyPair.split("=")
            val key = parts.first().trim()
            val value = parts.last().replace("\"", "")
            key to value
        }
    }

    private fun findElement(
        start: Int,
        tagEnd: Int,
        source: String,
        prefix: String,
        suffix: String
    ): Element? {
        val end = findEnd(start, source, suffix, prefix)

        when {
            start != -1 && end == -1 -> throw Exception("$name has no proper end!")
            (start == -1 || end < start) -> return null
        }
        val tag = source.substring(start + prefix.length, tagEnd)
        val attributes = determineAttributes(tag)
        val content = source.substring(tagEnd + 1, end)
        val elementEnd = end + suffix.length

        return Element(start, elementEnd, attributes, content)
    }

    private fun findEnd(
        start: Int,
        source: String,
        suffix: String,
        prefix: String
    ): Int {
        var end = source.indexOf(suffix, start)
        var nextStart = source.indexOf(prefix, start + 1)
        var nestCount = if (nextStart != -1 && nextStart < end) 1 else 0

        while (nestCount > 0) {
            nextStart = source.indexOf(prefix, nextStart + 1)
            end = source.indexOf(suffix, end + 1)
            if (nextStart != -1 && nextStart < end) {
                nestCount++
            } else {
                nestCount--
            }
        }
        return end
    }
}