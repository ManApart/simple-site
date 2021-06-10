package directives

interface Directive {
    fun compute(source: String, data: Map<String, Any>, scopedData: Map<String, Any> = mapOf<String, String>()): String
}