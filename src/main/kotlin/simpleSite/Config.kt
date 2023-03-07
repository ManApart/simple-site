package simpleSite

import com.fasterxml.jackson.module.kotlin.readValue
import blogBuilder.SiteConfig
import java.io.File


fun readConfig(): Map<String, Any> {
    return mapper.readValue(File("./src/main/resources/config.json"))
}

fun readSiteConfig(): SiteConfig {
    val globalConfig = readConfig()
    val folderPath = globalConfig["blogPath"]!! as String
    return mapper.readValue<SiteConfig>(File("$folderPath/config.json")).also { it.sourceFolder = folderPath }
}

fun readSiteConfig(sourceFolder: String): SiteConfig {
    return mapper.readValue<SiteConfig>(File("$sourceFolder/config.json")).also { it.sourceFolder = sourceFolder }
}
