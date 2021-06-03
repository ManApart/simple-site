class DomParser(private val name: String) {
    fun find(source: String): Element? {
        val prefix = "<$name"
        val suffix = "</$name>"
        val start = source.indexOf(prefix)
        val tagEnd = source.indexOf(">")
        val selfClosing = (source[tagEnd - 1] == '/')
        val end = if (selfClosing) tagEnd else source.indexOf(suffix)

        when {
            start != -1 && end == -1 -> throw Exception("$name has no proper end!")
            (start == -1 || end < start) -> return null
        }
        return if (selfClosing) {
            findSelfClosingElement(start, tagEnd, end, source, prefix)
        } else {
            findElement(start, tagEnd, end, source, prefix, suffix)
        }
    }

    private fun findElement(
        start: Int,
        tagEnd: Int,
        end: Int,
        source: String,
        prefix: String,
        suffix: String
    ): Element {
        val tag = source.substring(start + prefix.length, tagEnd)
        val attributes = determineAttributes(tag)
        val content = source.substring(tagEnd + 1, end)
        val elementEnd = end + suffix.length

        return Element(start, elementEnd, attributes, content)
    }

    private fun findSelfClosingElement(
        start: Int,
        tagEnd: Int,
        end: Int,
        source: String,
        prefix: String,
    ): Element {
        val tag = source.substring(start + prefix.length, tagEnd - 1)
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

//    private fun getBetween(prefix: String, suffix: String, source: String): String? {
//        val start = source.lastIndexOf(prefix)
//        val end = source.lastIndexOf(suffix)
//        return if (start == -1 || end == -1 || end < start) {
//            null
//        } else {
//            source.substring(start + prefix.length, end)
//        }
//    }
}