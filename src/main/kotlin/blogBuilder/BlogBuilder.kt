package blogBuilder

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import simpleSite.readConfig
import java.io.File


fun main() {
    //src folder should look like: "blogPath": "workspace\\website\\src"
    val folderPath = readConfig()["blogPath"]!! as String
    buildSite(folderPath)
}

fun buildSite(sourceFolder: String) {
    val files = parseFiles("$sourceFolder/blogs/")

    val transformed = process(files)

    File("$sourceFolder/../out/index.html").also {
        it.parentFile.mkdirs()
    }.writeText(transformed)

    val css = File("$sourceFolder/styles.css").readText()

    File("$sourceFolder/../out/styles.css").writeText(css)

}

fun parseFiles(sourceFolder: String): Map<String, String> {
    return File(sourceFolder).listFiles()!!.filter { it.isFile }.associate {
        it.name to it.readText()
    }
}

fun process(files: Map<String, String>): String {
    //parse each file to html
    //sort by date
    //add <link href="assets/css/styles.css" rel="stylesheet"> to top
    //join to html
    files.values.map { file ->
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(file)
        val html = HtmlGenerator(file, parsedTree, flavour).generateHtml()
        println("Here")
    }

    return "test"
}

