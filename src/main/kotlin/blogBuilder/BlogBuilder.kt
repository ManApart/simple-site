package blogBuilder

import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.util.data.MutableDataSet
import simpleSite.readSiteConfig
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import simpleSite.codeBlock.formatCodeBlocks


fun main() {
    val config = readSiteConfig()
    buildBlog(config)
}

fun buildBlog(config: SiteConfig) {
    val files = parseFiles("${config.sourceFolder}/${config.blogs}/")

    val processed = process(files, config.blogs)

    //Write individual entries
    processed.forEach { entry ->
        writeFile(config.sourceFolder, config.blogs, entry.name, entry.html, entry.name.replace("-", " "), config.homeLink)
    }

    //write a page for all entries
    val content = prepFullFile(processed, config.toc, config.tocTitle)
    writeFile(config.sourceFolder, config.blogs, "index", content, config.tabTitle, config.homeLink)

    val css = File("${config.sourceFolder}/styles.css").readText()

    File("${config.sourceFolder}/out/${config.blogs}/assets/css/styles.css").also {
        it.parentFile.mkdirs()
    }.writeText(css)

}

fun parseFiles(sourceFolder: String): Map<String, String> {
    return File(sourceFolder).listFiles()!!.filter { it.isFile }.associate {
        it.name to it.readText()
    }
}

fun process(files: Map<String, String>, subPath: String): List<Entry> {
    val options = MutableDataSet()
    options.set(Parser.EXTENSIONS, listOf(TablesExtension.create()))
    val parser: Parser = Parser.builder(options).build()
    val renderer: HtmlRenderer = HtmlRenderer.builder(options).build()

    return files.values
        .map { processSingleFile(it, subPath, parser, renderer) }
        .sortedByDescending { it.date }
}

private fun processSingleFile(fileText: String, subPath: String, parser: Parser, renderer: HtmlRenderer): Entry {
    val lines = fileText.split("\n")
    val name = lines[0]
        .replace("#", "")
        .trim()
    val cleanedName = name.replace(" ", "-")
    val dateText = lines[2].trim()

    val date = try {
        LocalDate.parse(dateText, DateTimeFormatter.ofPattern("M-dd-yyyy"))
    } catch (e: Exception) {
        LocalDate.MIN
    }

    val titleLine = "# [$name](/$subPath/$cleanedName.html)"
    //Don't add extra spaces to tables
    val properlySpaced = lines.subList(1, lines.size).map { if (it.contains("|")) it else "$it\n" }
    val toParse = (listOf(titleLine + "\n") + properlySpaced).joinToString("\n")
    val document: Node = parser.parse(toParse)

    var html = renderer.render(document)
        .replace("<body>", "")
        .replace("</body>", "")
        .replace("<h1>", "<h1 id=\"$cleanedName\">")
        .formatCodeBlocks()

    if (date != LocalDate.MIN) {
        html = html.replaceFirst(dateText, "<div class=\"entry-date\">$dateText</div>")
    }

    html = "<div class=\"entry\">$html</div>"

    return Entry(cleanedName, date, fileText, html)
}


fun prepFullFile(processed: List<Entry>, includeTOC: Boolean, tocTitle: String): String {
    val prefix = if (includeTOC) generateTOC(processed, tocTitle) else ""
    return prefix + processed.joinToString("\n") { it.html }
}

fun generateTOC(processed: List<Entry>, tocTitle: String): String {
    val contents = processed.joinToString("\n") {
        val name = it.name.replace("-", " ")
        "<li><a href=\"#${it.name}\">$name</a></li>"
    }
    return """<h1>$tocTitle</h1>
<div id="toc" tabindex="0">
  <img src="assets/images/list.svg" alt="Table of Contents"></img>
<ol>
$contents
</ol></div>"""
}

fun writeFile(sourceFolder: String, subPath: String, fileName: String, contents: String, tabTitle: String, homeLink: String) {
    val text = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <title>$tabTitle</title>
            <link href="/$subPath/assets/css/styles.css" rel="stylesheet">
            <link rel="shortcut icon" type="image/png" href="/$subPath/assets/images/favicon.png" />
        </head>
        <body>
            <div id="home-link"><a href="$homeLink"><img src="/$subPath/assets/images/home.svg"></img></a></div>
            $contents
        </body>
        </html>
    """.trimIndent()

    File("$sourceFolder/out/$subPath/$fileName.html").also {
        it.parentFile.mkdirs()
    }.writeText(text)
}
