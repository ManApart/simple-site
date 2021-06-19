package simpleSite

import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File


fun readConfig(): Map<String, Any> {
    return mapper.readValue(File("./src/main/resources/config.json"))
}

fun readSiteConfig(): Map<String, Any> {
    val globalConfig = readConfig()
    val folderPath = globalConfig["blogPath"]!! as String
    return mapper.readValue<Map<String, Any>>(File("$folderPath/config.json")).toMutableMap().also { it["folderPath"] = folderPath }
}
