package simpleSite

import simpleSite.directives.Directive

class Transformer(private val directiveName: String, private val directiveBuilder: (Element) -> Directive) {

    fun transform(input: String, context: Context): String {
        val parser = DomParser(directiveName)
        var transformed = input
        var element = parser.find(transformed)
        while (element != null) {
            val directive = directiveBuilder(element)
            transformed = directive.compute(transformed, context)
            element = parser.find(transformed)
        }

        return transformed
    }
}