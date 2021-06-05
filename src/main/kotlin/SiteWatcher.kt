import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds

fun main() {
    val text = File("./src/main/resources/config.json").readText()
    val config: Config = jacksonObjectMapper().readValue(text)
    val pathToWatch = File(config.folderPath).toPath()
    val cssPath = File(config.folderPath + "/css").toPath()
    val watchService = FileSystems.getDefault().newWatchService()

    val pathKey = pathToWatch.register(
        watchService, StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE
    )
    val pathKeyCss = cssPath.register(
        watchService, StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE
    )

    while (true) {
        Thread.sleep(500)
        val watchKey = watchService.take()

        if (watchKey.pollEvents().isNotEmpty()) {
            println("Building!")
            try {
                buildSite(config.folderPath)
            } catch (ex: Exception){
                println(ex.message)
            }
        }

        if (!watchKey.reset()) {
            watchKey.cancel()
            watchService.close()
            break
        }
    }

    pathKey.cancel()
    pathKeyCss.cancel()
}