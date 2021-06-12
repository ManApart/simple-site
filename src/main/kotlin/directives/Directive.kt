package directives

import Context

interface Directive {
    fun compute(source: String, context: Context): String
}