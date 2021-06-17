package blogBuilder

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import simpleSite.readConfig
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {
    //src folder should look like: "blogPath": "workspace\\website"
    //blog path look like: "blogPath": "blog"
    val config = readConfig()
    val folderPath = config["blogPath"]!! as String
    val subPath = config["blogSubPath"]!! as String
    buildBlog(folderPath, subPath)
}

fun buildBlog(sourceFolder: String, subPath: String) {
    val files = parseFiles("$sourceFolder/blogs/")

    val processed = process(files, subPath)

    //Write individual entries
    processed.forEach { entry ->
        writeFile(sourceFolder, subPath, entry.name, entry.html)
    }

    //write a page for all entries
    writeFile(sourceFolder, subPath, "index", processed.joinToString("\n") { it.html })

    val css = File("$sourceFolder/styles.css").readText()

    File("$sourceFolder/out/$subPath/assets/css/styles.css").also {
        it.parentFile.mkdirs()
    }.writeText(css)

}

fun parseFiles(sourceFolder: String): Map<String, String> {
    return File(sourceFolder).listFiles()!!.filter { it.isFile }.associate {
        it.name to it.readText()
    }
}

fun process(files: Map<String, String>, subPath: String): List<Entry> {
    val flavour = CommonMarkFlavourDescriptor()

    return files.values
        .map { processSingleFile(it, subPath, flavour) }
        .sortedByDescending { it.date }
}

private fun processSingleFile(fileText: String, subPath: String, flavour: CommonMarkFlavourDescriptor): Entry {
    val lines = fileText.split("\n")
    val name = lines[0]
        .replace("#", "")
        .trim()
    val cleanedName = name.replace(" ", "-")

    val titleLine = "# [$name](/$subPath/$cleanedName.html)"
    val toParse = (listOf(titleLine) + lines.subList(1, lines.size)).joinToString("\n")

    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(toParse)
    val html = HtmlGenerator(toParse, parsedTree, flavour).generateHtml()
        .replace("<body>", "")
        .replace("</body>", "")
        .replace("\n", "<br/>")


    val dateText = lines[2].trim()
    val date = LocalDate.parse(dateText!!, DateTimeFormatter.ofPattern("M-dd-yyyy"))
    return Entry(cleanedName, date, fileText, html)
}

fun writeFile(sourceFolder: String, subPath: String, fileName: String, contents: String) {
    val text = """
        <body>
            <link href="/$subPath/assets/css/styles.css" rel="stylesheet">
            $contents
        </body>
    """.trimIndent()

    File("$sourceFolder/out/$subPath/$fileName.html").also {
        it.parentFile.mkdirs()
    }.writeText(text)
}
