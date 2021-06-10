package directives

import Element

data class Include(val start: Int, val end: Int, val src: String) : Directive {
    constructor(element: Element) : this(
        element.start,
        element.end,
        element.attributes["src"] ?: throw IllegalArgumentException("No Src found!")
    )

    override fun compute(source: String, files: Map<String, Any>, scopedData: Map<String, Any>): String {
        return source.substring(0, start) + files[src] + source.substring(end, source.length)
    }

}