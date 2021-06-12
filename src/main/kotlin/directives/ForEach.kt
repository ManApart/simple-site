package directives

import Context
import Element
import convert
import getNestedValue
import ifNotNuller
import ifNuller
import interpolate
import looper
import transformHtml

class ForEach(
    private val start: Int,
    private val end: Int,
    private val indexName: String,
    private val sourceKeyPath: String,
    private val template: String
) : Directive {
    constructor(element: Element) : this(
        element.start,
        element.end,
        element.attributes["i"] ?: throw IllegalArgumentException("No index found!"),
        element.attributes["src"] ?: throw IllegalArgumentException("No Src found!"),
        element.content
    )

    override fun compute(source: String, context: Context): String {
        val list = (context.data.getNestedValue(sourceKeyPath.split("."))
            ?: context.scopedData.getNestedValue(sourceKeyPath.split("."))) as List<*>

        val repeated = list.filterNotNull().joinToString("") {
            val newContext = context.copy(scopedData = mapOf(indexName to it))
            transformHtml(template, newContext)
        }
        return source.substring(0, start) + repeated + source.substring(end, source.length)
    }


}