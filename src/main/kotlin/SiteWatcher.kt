import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds

fun main(){
    val text = File("./src/main/resources/config.json").readText()
    val config: Config = jacksonObjectMapper().readValue(text)
    val currentDirectory  = File(config.folderPath)
    val watchService = FileSystems.getDefault().newWatchService()
    val pathToWatch = currentDirectory.toPath()

    val pathKey = pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE)

    while (true) {
        Thread.sleep(500)
        val watchKey = watchService.take()

       if (watchKey.pollEvents().isNotEmpty()){
           buildSite(config.folderPath)
       }

        if (!watchKey.reset()) {
            watchKey.cancel()
            watchService.close()
            break
        }
    }

    pathKey.cancel()
}