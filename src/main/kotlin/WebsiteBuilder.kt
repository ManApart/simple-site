import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import directives.ForEach
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

    val included = include(files["index.html"]!!, files)
    val looped = loop(included, data)
    val interpolated = interpolate(looped, data)

    File("$sourceFolder/../out/index.html").also {
        it.parentFile.mkdirs()
    }.writeText(interpolated)
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

fun interpolate(input: String, data: Map<String, Any>, scopedData: Map<String, Any> = mapOf()): String {
    var interpolated = input
    var directive = Interpolation.find(interpolated)
    while (directive != null) {
        interpolated = directive.compute(interpolated, data, scopedData)
        directive = Interpolation.find(interpolated)
    }

    return interpolated
}

fun loop(input: String, data: Map<String, Any>): String {
    val parser = DomParser("for")
    var looped = input
    var directive = parser.find(looped)
    while (directive != null) {
        looped = ForEach(directive).compute(looped, data)
        directive = parser.find(looped)
    }

    return looped
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


fun <K, V> Map<K, V>.getNestedValue(keys: List<String>): Any? {
    return if (keys.size == 1){
        getByName(keys.first())
    } else {
        val newMap = getByName(keys.first())
        if (newMap is List<*>){
            throw IllegalStateException("Can't handle arrays: $newMap")
        } else if (newMap == null){
            return null
        }
        (newMap as Map<String, Any>).getNestedValue(keys.subList(1, keys.size))
    }
}

private fun <K, V> Map<K, V>.getByName(name: String): V? {
    return entries.firstOrNull { it.key == name }?.value
//    return entries.firstOrNull { it.key == name }?.value value?: throw IllegalArgumentException("No value for $name")
}