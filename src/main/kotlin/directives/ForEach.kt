package websiteBuilder.directives

import websiteBuilder.getBetween


class ForEach(
    val start: Int,
    val end: Int,
    val indexName: String,
    val sourceKeyPath: String,
    val template: String
) {
    fun compute(source: String, files: Map<String, String>, data: Map<String, Any>): String {
        return ""
//        return source.substring(0, start) + files[src] + source.substring(end, source.length)
    }

    companion object {
        fun find(source: String): Include? {
            val prefix = "<for"
            val suffix = "/>"
            val start = source.indexOf(prefix)
            val end = source.indexOf(suffix)
            return when {
                start != -1 && end == -1 -> throw Exception("Include has no proper end!")
                (start == -1 || end < start) -> null
                else -> {
                    val inner = source.substring(start + prefix.length, end)
                    val src = getBetween("src=\"", "\"", inner)
                    if (src == null) {
                        throw Exception("Include had no source!")
                    } else {
                        Include(start, end + suffix.length, src)
                    }
                }
            }
        }
    }
}