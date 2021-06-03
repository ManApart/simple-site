import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import directives.Include
import directives.Interpolation
import java.io.File

val mapper = jacksonObjectMapper()

fun main() {
    //src folder should look like: "folderPath": "workspace\\website\\src"
    val text = File("./src/main/resources/config.json").readText()
    val config: Config = jacksonObjectMapper().readValue(text)
    buildSite(config.folderPath)
}

fun buildSite(sourceFolder: String) {
    val files = parseFiles(sourceFolder)
    val data = parseData(sourceFolder)

    val included = interpolate(include(files["index.html"]!!, files), data)

    File("$sourceFolder/../out/index.html").also {
        it.parentFile.mkdirs()
    }.writeText(included)
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

fun include(input: String, files: Map<String, String>): String {
    val parser = DomParser("include")
    var included = input
    var directive = parser.find(included)
    while (directive != null) {
        included = Include(directive).compute(included, files)
        directive = parser.find(included)
    }

    return included
}

fun interpolate(input: String, data: Map<String, Any>): String {
    var included = input
    var directive = Interpolation.find(included)
    while (directive != null) {
        included = directive.compute(included, data)
        directive = Interpolation.find(included)
    }

    return included
}

fun getBetween(prefix: String, suffix: String, source: String): String? {
    val start = source.lastIndexOf(prefix)
    val end = source.lastIndexOf(suffix)
    return if (start == -1 || end == -1 || end < start) {
        null
    } else {
        source.substring(start + prefix.length, end)
    }
}
