package simpleSite.codeBlock

interface Matcher {
    fun getNext(line: String, start: Int): Pair<Int, Int>?
}

class Cap(val starts: List<String>, val ends: List<String>, val includeStart: Boolean = false, val includeEnd: Boolean = false){
    constructor(start: String, end: String, includeStart: Boolean = false, includeEnd: Boolean = false): this(listOf(start), listOf(end), includeStart, includeEnd)
    fun getNext(line: String, start: Int): Pair<Int, Int>? {
        return null
    }
}

class Identifier(val caps: List<Cap>) : Matcher {
    override fun getNext(line: String, start: Int): Pair<Int, Int>? {
        //find all matching caps
        //return the one with the earliest start
        caps.mapNotNull { it.getNext(line, start) }
        return null
    }
}

class ExactMatcher(private val match: String) : Matcher {
    override fun getNext(line: String, start: Int): Pair<Int, Int>? {
        val matchStart = line.indexOf(match, start)
        val end = matchStart + match.length
        return if (matchStart != -1 && end != -1) Pair(matchStart, end) else null
    }
}

class TokenType(className: String, matcher: Matcher) {
    fun parse(line: String): List<Token>{
        //keep calling getNext until it returns empty
        return emptyList()
    }
}

//These are coordinates of the OG string, but will be wrong once we manipulate it
//Carry offset for how much longer the string is?
//Will work as long as there is not string offset
class Token(val start: Int, val end: Int, val className: String) {
    fun embellish(line: String): String {
        return line
    }

}