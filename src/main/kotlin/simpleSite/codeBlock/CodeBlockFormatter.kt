package simpleSite.codeBlock

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

private val tokenTypes = listOf(
    TokenType("hljs-keyword", ExactMatcher(listOf("class", "object", "external"))),
    TokenType("hljs-function", ExactMatcher(listOf("fun"))),
    TokenType("hljs-meta", CapMatcher(listOf(
        Cap(listOf("@"), listOf(" ", "\n", "("), true)
    ))),
    TokenType("hljs-string", CapMatcher(Cap("\"", "\""))),
    TokenType("hljs-title", CapMatcher(Cap(listOf(".", "fun "), listOf("(")))),
    TokenType("hljs-type", CapMatcher(Cap(listOf(": "), listOf(")", ",", " ", "\n")))),
)

fun formatCodeBlocks(htmlText: String): String {
    val doc = Jsoup.parse(htmlText)
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
    return tokenTypes.flatMap { it.parse(block) }
}