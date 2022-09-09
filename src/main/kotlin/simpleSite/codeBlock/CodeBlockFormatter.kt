package simpleSite.codeBlock

import org.jsoup.Jsoup

fun formatCodeBlocks(htmlText: String): String {
    val doc = Jsoup.parse(htmlText)
    val codeElements = doc.select("code")
    codeElements.forEach { it.addClass("hljs") }

    return doc.html()
}