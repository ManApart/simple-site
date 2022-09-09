package simpleSite.codeBlock

import org.jsoup.Jsoup

fun formatCodeBlocks(htmlText: String): String {
    val doc = Jsoup.parse(htmlText)
    val code = doc.select("code")
    println(code.html())
    return htmlText
}