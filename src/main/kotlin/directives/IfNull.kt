package directives

import Element
import getNestedValue

data class IfNull(
    val start: Int,
    val end: Int,
    private val sourceKeyPath: String,
    val content: String,
    val lookingForNull: Boolean
) : Directive{
    constructor(element: Element, lookingForNull: Boolean) : this(element.start, element.end, element.attributes["src"] ?: throw IllegalArgumentException("No Src found!"), element.content, lookingForNull)

    override fun compute(
        source: String, data: Map<String, Any>,
        scopedData: Map<String, Any>
    ): String {
        val data = (data.getNestedValue(sourceKeyPath.split("."))
            ?: scopedData.getNestedValue(sourceKeyPath.split(".")))

        val replacement = when{
            lookingForNull && data == null -> content
            !lookingForNull && data != null -> content
            else -> ""
        }
//        val replacement = if (data == null) content else ""
        return source.substring(0, start) + replacement + source.substring(end, source.length)
    }

}