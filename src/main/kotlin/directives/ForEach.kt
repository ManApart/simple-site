package directives

import getNestedValue

class ForEach(
    private val start: Int,
    private val end: Int,
    private val indexName: String,
    private val sourceKeyPath: String,
    private val template: String
) {
    fun compute(source: String, data: Map<String, Any>): String {
        val list = data.getNestedValue(sourceKeyPath.split(".")) as List<*>
        val repeated = template.repeat(list.size)
        return source.substring(0, start) + repeated + source.substring(end, source.length)
    }

}