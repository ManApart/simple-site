package directives

import Element
import getNestedValue

data class IfNotNull(
    val start: Int,
    val end: Int,
    private val sourceKeyPath: String,
    val content: String
) {
    constructor(element: Element) : this(element.start, element.end, element.attributes["src"] ?: throw IllegalArgumentException("No Src found!"), element.content)

    fun compute(
        source: String, data: Map<String, Any>,
        scopedData: Map<String, Any> = mapOf<String, String>()
    ): String {
        val data = (data.getNestedValue(sourceKeyPath.split("."))
            ?: scopedData.getNestedValue(sourceKeyPath.split(".")))
        val replacement = if (data != null) content else ""
        return source.substring(0, start) + replacement + source.substring(end, source.length)
    }

}