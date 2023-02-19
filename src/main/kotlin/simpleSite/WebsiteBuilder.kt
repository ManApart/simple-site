package simpleSite

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import simpleSite.directives.*
import java.io.File

val mapper = jacksonObjectMapper()

val looper = Transformer("for") { ForEach(it) }
val includer = Transformer("include") { Include(it) }
val ifNotNuller = Transformer("ifnotnull") { IfNull(it, false) }
val ifNuller = Transformer("ifnull") { IfNull(it, true) }

fun main() {
    val folderPath = readConfig()["folderPath"]!! as String
    buildSite(folderPath)
}

fun buildSite(sourceFolder: String) {
    val files = parseFiles(sourceFolder)
    val data = parseData(sourceFolder)

    files.entries.filter { it.key.startsWith("index") }.forEach { (fileName, html)->
        createPage(fileName, html, data, files, sourceFolder)
    }

    val css = File("$sourceFolder/css/").listFiles()!!
        .joinToString("\n") { it.readText() }

    File("$sourceFolder/../out/styles.css").writeText(css)

}

private fun createPage(
    pageName: String,
    pageHtml: String,
    data: Map<String, Any>,
    files: Map<String, String>,
    sourceFolder: String
) {
    val transformed = transformHtml(pageHtml, Context(data, files, mapOf()))
    val newPageName = if (pageName == "index.html") pageName else pageName.replace("index", "")
    val outName = "$sourceFolder/../out/${newPageName}"
    File(outName).also {
        it.parentFile.mkdirs()
    }.writeText(transformed)
}

fun parseFiles(sourceFolder: String): Map<String, String> {
    return File(sourceFolder).listFiles()!!.filter { it.isFile }.associate {
        it.name to it.readText()
    }
}

fun parseData(sourceFolder: String): Map<String, Any> {
    return File("$sourceFolder/data/").listFiles()!!.associate {
        it.name.replace(".json", "") to mapper.readValue(it.readText())
    }
}

fun transformHtml(source: String, context: Context): String {
    return source.convert(includer, context)
        .convert(looper, context)
        .convert(ifNuller, context)
        .convert(ifNotNuller, context)
        .interpolate(context)
}

fun String.convert(transformer: Transformer, context: Context): String {
    return transformer.transform(this, context)
}



