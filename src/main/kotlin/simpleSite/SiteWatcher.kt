package simpleSite

import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds

fun main() {
    val folderPath = readConfig()["folderPath"]!! as String
    val pathToWatch = File(folderPath).toPath()
    val cssPath = File(folderPath + "/css").toPath()
    val watchService = FileSystems.getDefault().newWatchService()

    val pathKey = pathToWatch.register(
        watchService, StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE
    )
    val pathKeyCss = cssPath.register(
        watchService, StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE
    )

    buildSite(folderPath)
    var lastBuild = System.currentTimeMillis()
    while (true) {
        val watchKey = watchService.take()

        if (watchKey.pollEvents().isNotEmpty()) {
            if (System.currentTimeMillis() > lastBuild + 500) {
                Thread.sleep(100)
                try {
                    buildSite(folderPath)
                } catch (ex: Exception) {
                    println(ex.message)
                }
                lastBuild = System.currentTimeMillis()
                println("Built at $lastBuild")
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