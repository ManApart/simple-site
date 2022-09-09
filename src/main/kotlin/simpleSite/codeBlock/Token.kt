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

class TokenType(private val className: String, private val matcher: Matcher) {
    fun parse(line: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var token = matcher.getNext(line, 0)
        while(token != null){
            tokens.add(Token(token.first, token.second, className))
            token = matcher.getNext(line, token.second)
        }

        return tokens
    }
}

class Token(val start: Int, private val end: Int, private val className: String) {
    fun embellish(line: String): String {
        return line.substring(0, start) + "<span class=\"$className\">" + line.substring(start, end) + "</span>" + line.substring(end, line.length)
    }

}