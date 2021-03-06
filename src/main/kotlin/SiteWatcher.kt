import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds

fun watch(path: String, secondPath: String, builder: () -> Unit) {
    val pathToWatch = File(path).toPath()
    val pathToWatch2 = File(secondPath).toPath()
    val watchService = FileSystems.getDefault().newWatchService()

    val pathKey = pathToWatch.register(
        watchService, StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE
    )
    val pathKey2 = pathToWatch2.register(
        watchService, StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE
    )

    builder()
    var lastBuild = System.currentTimeMillis()
    println("Starting to watch $pathToWatch and $pathToWatch2")
    while (true) {
        val watchKey = watchService.take()

        if (watchKey.pollEvents().isNotEmpty()) {
            if (System.currentTimeMillis() > lastBuild + 500) {
                Thread.sleep(100)
                try {
                    builder()
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
    pathKey2.cancel()
}