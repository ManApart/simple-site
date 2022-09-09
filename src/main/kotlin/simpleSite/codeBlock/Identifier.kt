package simpleSite.codeBlock

interface Matcher {
    fun getNext(line: String, start: Int): Pair<Int, Int>?
}

class Cap(val starts: List<String>, val ends: List<String>, val includeStart: Boolean = false, val includeEnd: Boolean = false) {
    constructor(start: String, end: String, includeStart: Boolean = false, includeEnd: Boolean = false) : this(listOf(start), listOf(end), includeStart, includeEnd)

    fun getNext(line: String, start: Int): Pair<Int, Int>? {
        return null
    }
}

class Identifier(private val caps: List<Cap>) : Matcher {
    override fun getNext(line: String, start: Int): Pair<Int, Int>? {
        //find all matching caps
        //return the one with the earliest start
        caps.mapNotNull { it.getNext(line, start) }
        return null
    }
}

class ExactMatcher(private val matches: List<String>) : Matcher {
    constructor(match: String) : this(listOf(match))

    override fun getNext(line: String, start: Int): Pair<Int, Int>? {
        return matches.mapNotNull { match ->
            val matchStart = line.indexOf(match, start)
            val end = matchStart + match.length
            if (matchStart != -1 && end != -1) Pair(matchStart, end) else null
        }.minByOrNull { it.first }
    }
}

class TokenType(className: String, matcher: Matcher) {
    fun parse(line: String): List<Token> {
        //keep calling getNext until it returns empty
        return emptyList()
    }
}

//These are coordinates of the OG string, but will be wrong once we manipulate it
//Must be called working from back of the string towards the front
class Token(val start: Int, val end: Int, val className: String) {
    fun embellish(line: String): String {
        return line
    }

}