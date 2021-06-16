package blogBuilder

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import simpleSite.readConfig
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * TODO
 * - set up self links for each entry
 */

fun main() {
    //src folder should look like: "blogPath": "workspace\\website"
    val folderPath = readConfig()["blogPath"]!! as String
    buildBlog(folderPath)
}

fun buildBlog(sourceFolder: String) {
    val files = parseFiles("$sourceFolder/blogs/")

    val processed = process(files)

    //Write individual entries
    processed.forEach { entry ->
        writeFile(sourceFolder, entry.name, entry.html)
    }

    //write a page for all entries
    writeFile(sourceFolder, "index", processed.joinToString("\n") { it.html })

    val css = File("$sourceFolder/styles.css").readText()

    File("$sourceFolder/out/assets/css/styles.css").writeText(css)

}

fun parseFiles(sourceFolder: String): Map<String, String> {
    return File(sourceFolder).listFiles()!!.filter { it.isFile }.associate {
        it.name to it.readText()
    }
}

fun process(files: Map<String, String>): List<Entry> {
    val flavour = CommonMarkFlavourDescriptor()

    return files.values
        .map { processSingleFile(it, flavour) }
        .sortedByDescending { it.date }
}

fun writeFile(sourceFolder: String, fileName: String, contents: String) {
    val text = """
        <body>
            <link href="assets/css/styles.css" rel="stylesheet">
            $contents
        </body>
    """.trimIndent()

    File("$sourceFolder/out/$fileName.html").also {
        it.parentFile.mkdirs()
    }.writeText(text)
}

private fun processSingleFile(fileText: String, flavour: CommonMarkFlavourDescriptor): Entry {
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(fileText)
    val html = HtmlGenerator(fileText, parsedTree, flavour).generateHtml()
        .replace("<body>", "")
        .replace("</body>", "")
        .replace("\n", "<br/>")

    val lines = fileText.split("\n")
    val name = lines[0]
        .replace("#", "")
        .trim()
        .replace(" ", "-")
    val dateText = lines[2].trim()
    val date = LocalDate.parse(dateText!!, DateTimeFormatter.ofPattern("M-dd-yyyy"))
    return Entry(name, date, fileText, html)
}

