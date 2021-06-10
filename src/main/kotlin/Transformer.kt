import directives.Directive

class Transformer(private val directiveName: String, private val directiveBuilder: (Element) -> Directive) {

    fun transform(input: String, data: Map<String, Any>, scopedData: Map<String, Any> = mapOf()): String {
        val parser = DomParser(directiveName)
        var transformed = input
        var element = parser.find(transformed)
        while (element != null) {
            val directive = directiveBuilder(element)
            transformed = directive.compute(transformed, data, scopedData)
            element = parser.find(transformed)
        }

        return transformed
    }
}