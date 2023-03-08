import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds

fun watch(paths: List<String>, builder: () -> Unit) {
    val watchService = FileSystems.getDefault().newWatchService()

    val watchKeys = paths.map { File(it).toPath() }.map {
        it.register(
            watchService, StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE
        )
    }

    builder()
    var lastBuild = System.currentTimeMillis()
    println("Starting to watch ${paths.joinToString()}")
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

    watchKeys.forEach { it.cancel() }
}