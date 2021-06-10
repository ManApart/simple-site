import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import directives.ForEach
import directives.IfNotNull
import directives.Include
import directives.Interpolation
import java.io.File

val mapper = jacksonObjectMapper()

val looper = Transformer("for") { ForEach(it) }
val includer = Transformer("include") { Include(it) }
val ifNotNuller = Transformer("ifnotnull") { IfNotNull(it) }

fun main() {
    //src folder should look like: "folderPath": "workspace\\website\\src"
    val text = File("./src/main/resources/config.json").readText()
    val config: Config = jacksonObjectMapper().readValue(text)
    buildSite(config.folderPath)
}

fun buildSite(sourceFolder: String) {
    val files = parseFiles(sourceFolder)
    val data = parseData(sourceFolder)

    val transformed = files["index.html"]!!
        .convert(includer, files)
        .convert(looper, data)
        .convert(ifNotNuller, data)
        .interpolate(data)

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

fun String.convert(transformer: Transformer, data: Map<String, Any>, scopedData: Map<String, Any> = mapOf()): String {
    return transformer.transform(this, data, scopedData)
}

fun String.interpolate(data: Map<String, Any>): String {
    return interpolate(this, data)
}

fun interpolate(input: String, data: Map<String, Any>, scopedData: Map<String, Any> = mapOf()): String {
    var interpolated = input
    var directive = Interpolation.find(interpolated)
    while (directive != null) {
        interpolated = directive.compute(interpolated, data, scopedData)
        directive = Interpolation.find(interpolated, directive.end)
    }

    return interpolated
}

fun <K, V> Map<K, V>.getNestedValue(keys: List<String>): Any? {
    return if (keys.size == 1) {
        getByName(keys.first())
    } else {
        val newMap = getByName(keys.first())
        if (newMap is List<*>) {
            throw IllegalStateException("Can't handle arrays: $newMap")
        } else if (newMap == null) {
            return null
        }
        (newMap as Map<String, Any>).getNestedValue(keys.subList(1, keys.size))
    }
}

private fun <K, V> Map<K, V>.getByName(name: String): V? {
    return entries.firstOrNull { it.key == name }?.value
//    return entries.firstOrNull { it.key == name }?.value value?: throw IllegalArgumentException("No value for $name")
}

