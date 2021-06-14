package simpleSite.directives

import simpleSite.Context

interface Directive {
    fun compute(source: String, context: Context): String
}