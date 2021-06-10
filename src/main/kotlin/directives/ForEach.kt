package directives

import Element
import getNestedValue
import interpolate
import looper

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

    override fun compute(source: String, data: Map<String, Any>, scopedData: Map<String, Any>): String {
        val list = (data.getNestedValue(sourceKeyPath.split("."))
            ?: scopedData.getNestedValue(sourceKeyPath.split("."))) as List<*>
        val repeated = list.filterNotNull().joinToString("") { computeTemplate(template, data, mapOf(indexName to it)) }
        return source.substring(0, start) + repeated + source.substring(end, source.length)
    }

    private fun computeTemplate(template: String, data: Map<String, Any>, scopedData: Map<String, Any>): String {
        return looper.transform(interpolate(template, data, scopedData), data, scopedData)
    }

}