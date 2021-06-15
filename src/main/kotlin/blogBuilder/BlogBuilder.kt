package blogBuilder

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import simpleSite.readConfig
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun main() {
    //src folder should look like: "blogPath": "workspace\\website\\src"
    val folderPath = readConfig()["blogPath"]!! as String
    buildBlog(folderPath)
}

fun buildBlog(sourceFolder: String) {
    val files = parseFiles("$sourceFolder/blogs/")

    val transformed = process(files)

    File("$sourceFolder/out/index.html").also {
        it.parentFile.mkdirs()
    }.writeText(transformed)

    val css = File("$sourceFolder/styles.css").readText()

    File("$sourceFolder/out/styles.css").writeText(css)

}

fun parseFiles(sourceFolder: String): Map<String, String> {
    return File(sourceFolder).listFiles()!!.filter { it.isFile }.associate {
        it.name to it.readText()
    }
}

fun process(files: Map<String, String>): String {
    val flavour = CommonMarkFlavourDescriptor()

    val contents = files.values
        .map { processSingleFile(it, flavour) }
        .sortedByDescending { it.date }
        .joinToString("\n") { it.html }

    return """
        <body>
            <link href="assets/css/styles.css" rel="stylesheet">
            $contents
        </body>
    """.trimIndent()
}

private fun processSingleFile(fileText: String, flavour: CommonMarkFlavourDescriptor): Entry {
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(fileText)
    val html = HtmlGenerator(fileText, parsedTree, flavour).generateHtml()
        .replace("<body>", "")
        .replace("</body>", "")
        .replace("\n", "<br/>")
    val dateText = fileText.split("\n")[2].trim()
    val date = LocalDate.parse(dateText!!, DateTimeFormatter.ofPattern("M-dd-yyyy"))
    return Entry(date, fileText, html)
}

