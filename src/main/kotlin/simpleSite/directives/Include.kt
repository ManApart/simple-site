package simpleSite.directives

import simpleSite.Context
import simpleSite.Element

data class Include(val start: Int, val end: Int, val src: String) : Directive {
    constructor(element: Element) : this(
        element.start,
        element.end,
        element.attributes["src"] ?: throw IllegalArgumentException("No Src found!")
    )

    override fun compute(source: String, context: Context): String {
        return source.substring(0, start) + context.files[src] + source.substring(end, source.length)
    }

}