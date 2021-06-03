package directives

import Element

data class Delete(val start: Int, val end: Int, val content: String) {
    constructor(element: Element) : this(element.start, element.end, element.content)

    fun compute(source: String): String {
        return source.substring(0, start) + content + source.substring(end, source.length)
    }

}