package websiteBuilder.directives

import websiteBuilder.getBetween


data class Include(val start: Int, val end: Int, val src: String) {

    fun compute(source: String, files: Map<String, String>): String {
        return source.substring(0, start) + files[src] + source.substring(end, source.length)
    }

    companion object {
        fun find(source: String): Include? {
            val prefix = "<include"
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