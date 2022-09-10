package simpleSite.codeBlock

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/*
Highlight syntax in code blocks. So a naive version of higlightjs, but at generation time instead of runtime
https://highlightjs.org/
 */
private val tokenTypes = listOf(
    TokenType("hljs-keyword", ExactMatcher(listOf("class", "object", "external", "return", "val ", "var ", "as "))),
    TokenType("hljs-function", ExactMatcher(listOf("fun"))),
    TokenType("hljs-meta", CapMatcher(listOf(
        Cap(listOf("@"), listOf(" ", "\n", "("), includeStart = true)
    ))),
    TokenType("hljs-string", CapMatcher(Cap("\"", "\""))),
    TokenType("hljs-title", CapMatcher(Cap(listOf(".", "fun "), listOf("(", " ", "{")))),
    TokenType("hljs-type", CapMatcher(Cap(listOf(": ", "as "), listOf(")", ",", " ", "\n")))),
)

fun String.formatCodeBlocks(): String {
    val doc = Jsoup.parse(this)
    doc.select("code").forEach { formatCodeBlock(it) }

    return doc.html()
}

private fun formatCodeBlock(block: Element) {
    block.addClass("hljs")
    var html = block.html()
    getTokens(block.html())
        .sortedByDescending { it.start }
        .forEach { html = it.embellish(html) }

    block.html(html)
}

private fun getTokens(block: String): List<Token> {
    val allTokens = tokenTypes.flatMap { it.parse(block) }
    return allTokens.filterNot { allTokens.isSubToken(it) }
}

//Only take the top level tokens, ignore spans inside of spans
private fun List<Token>.isSubToken(token: Token): Boolean {
    return any { possibleParent ->
        possibleParent.start < token.start && possibleParent.end > token.end
    }
}
