package directives

import Element

data class Include(val start: Int, val end: Int, val src: String) {
    constructor(element: Element) : this (element.start, element.end, element.attributes["src"] ?: throw IllegalArgumentException("No Src found!"))

    fun compute(source: String, files: Map<String, String>): String {
        return source.substring(0, start) + files[src] + source.substring(end, source.length)
    }

}