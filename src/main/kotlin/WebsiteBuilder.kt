import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import directives.*
import java.io.File

val mapper = jacksonObjectMapper()

val looper = Transformer("for") { ForEach(it) }
val includer = Transformer("include") { Include(it) }
val ifNotNuller = Transformer("ifnotnull") { IfNull(it, false) }
val ifNuller = Transformer("ifnull") { IfNull(it, true) }

fun main() {
    //src folder should look like: "folderPath": "workspace\\website\\src"
    val text = File("./src/main/resources/config.json").readText()
    val config: Config = jacksonObjectMapper().readValue(text)
    buildSite(config.folderPath)
}

fun buildSite(sourceFolder: String) {
    val files = parseFiles(sourceFolder)
    val data = parseData(sourceFolder)

    val transformed = transformHtml(files["index.html"]!!, Context(data, files, mapOf()))

    File("$sourceFolder/../out/index.html").also {
        it.parentFile.mkdirs()
    }.writeText(transformed)

    val css = File("$sourceFolder/css/").listFiles()!!
        .joinToString("\n") { it.readText() }

    File("$sourceFolder/../out/styles.css").writeText(css)

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



