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
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import simpleSite.codeBlock.formatCodeBlocks

val dateFormat = DateTimeFormatter.ofPattern("M-dd-yyyy")

fun main() {
    val config = readSiteConfig()
    buildBlog(config)
}

fun buildBlog(config: SiteConfig) {
    val files = parseFiles("${config.sourceFolder}/${config.blogs}/")

    val processed = process(files, config.blogs)

    //Write individual entries
    processed.forEach { entry ->
        writeFile(config.sourceFolder, config.blogs, entry.name, entry.name.replace("-", " "), config.homeLink, "blog-entry", "blog-entry-view", entry.html)
    }

    //write a page for all entries
    if (config.singlePageToc) {
        writeFile(config.sourceFolder, config.blogs, "index", config.tabTitle, config.homeLink, "blogs", "blogs-toc-view") { prepFullPageToc(processed, config.tocTitle) }
    } else {
        writeFile(config.sourceFolder, config.blogs, "index", config.tabTitle, config.homeLink, "blogs", "blogs-toc-view") {
            if (config.toc) generateTOC(processed, config.tocTitle)
            processed.forEach { unsafe { it.html } }
        }
    }
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
//    html = "<div class=\"entry\" style=\"view-transition-name: ${cleanedName}\">$html</div>"

    return Entry(cleanedName, date, fileText, html)
}


fun BODY.generateTOC(processed: List<Entry>, tocTitle: String) {
    h1 { +tocTitle }
    div {
        id = "toc"
        tabIndex = "0"
        img("Table of Contents") { src = "assets/images/list.svg" }
        div("toc-list") {
            ol {
                processed.forEach { entry ->
                    li("toc-entry") {
                        a {
                            href = "#${entry.name}"
                            +entry.name
                        }
                    }
                }
            }
        }
    }
}

fun BODY.prepFullPageToc(processed: List<Entry>, tocTitle: String) {
    val blogByYear = processed.groupBy { it.date.year }
    h1 { +tocTitle }
    div {
        id = "toc"
        blogByYear.forEach { (year, entries) ->
            div("toc-year") {
                h2 { +"$year" }
                ol("toc-year-entries") {
                    entries.forEach { entry ->
                        li {
//                            style = "view-transition-name: ${entry.name}"
                            a("${entry.name}.html", classes = "toc-entry") {
                                +entry.name.replace("-", " ")
                                p("toc-entry-date") { +dateFormat.format(entry.date) }
                            }
                            div("small-line-break")
                        }
                    }
                }
            }
        }
    }
}

fun writeFile(sourceFolder: String, subPath: String, fileName: String, tabTitle: String, homeLink: String, pageId: String, bodyClasses: String, contents: String) {
    writeFile(sourceFolder, subPath, fileName, tabTitle, homeLink, pageId, bodyClasses) {
        unsafe { +contents }
    }
}

fun writeFile(sourceFolder: String, subPath: String, fileName: String, tabTitle: String, homeLink: String, pageId: String, bodyClasses: String, contents: BODY.() -> Unit) {
    val text = StringBuffer().appendHTML().html {
        attributes["data-page"] = pageId
        head {
            title(tabTitle)
            link("/$subPath/assets/css/styles.css", "stylesheet")
            link("/$subPath/assets/css/images/favicon.png", "shortcut icon", "image/png")
            meta("view-transition", "same-origin")
        }
        body(bodyClasses) {
            div {
                id = "home-link"
                a {
                    href = homeLink
                    img { src = "/$subPath/assets/images/home.svg" }
                }
            }
            contents()
        }
    }.toString()
    File("$sourceFolder/out/$subPath/$fileName.html").also {
        it.parentFile.mkdirs()
    }.writeText(text)
}
