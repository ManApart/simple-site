package simpleSite

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File


fun readConfig() : Map<String, Any> {
    return jacksonObjectMapper().readValue(File("./src/main/resources/config.json"))
}
