package websiteBuilder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

@JsonIgnoreProperties(ignoreUnknown = true)
class Config(val folderPath: String = ".")

fun readConfig() : Config {
    val file = Config::class.java.getResource("./config.json")
    return jacksonObjectMapper().readValue(file)
}
