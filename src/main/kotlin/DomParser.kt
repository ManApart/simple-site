package websiteBuilder

import websiteBuilder.directives.Include

class DomParser(private val name: String, private val attributeNames: List<String>) {
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
        val content = source.substring(tagEnd, end)

        return Element(start, end, attributes, content)
    }

    private fun determineAttributes(tag: String): Map<String, String> {
        return mapOf()
    }

    private fun find2(source: String): Include? {
        val prefix = "<include"
        val suffix = "/>"
        val start = source.indexOf(prefix)
        val end = source.indexOf(suffix)
        return when {
            start != -1 && end == -1 -> throw Exception("Include has no proper end!")
            (start == -1 || end < start) -> null
            else -> {
                val inner = source.substring(start + prefix.length, end)
                val src = websiteBuilder.getBetween("src=\"", "\"", inner)
                if (src == null) {
                    throw Exception("Include had no source!")
                } else {
                    Include(start, end + suffix.length, src)
                }
            }
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