import blogBuilder.buildBlog
import simpleSite.buildSite
import simpleSite.readSiteConfig
import java.io.File
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    if (args.size != 2){
        throw IllegalArgumentException("Must include build type and path!\n" + help())
    }
    println("Type: " + args[0])
    println("Source: " + args[1])

    val sourceFolder = File(args[1]).canonicalFile
    if (!sourceFolder.exists() || !sourceFolder.isDirectory){
        throw IllegalArgumentException("Invalid source folder: ${args[1]}.\n ${help()}")
    }
    val source = sourceFolder.absolutePath

    when (args.first().lowercase()){
        "build-site" -> buildSite(source)
        "watch-site" -> {
            val cssPath = "$source/css"
            watch(listOf(source, cssPath)) { buildSite(source) }
        }
        "build-blog" -> buildBlog(readSiteConfig(source))
        "watch-blog" -> {
            val config = readSiteConfig(source)
            watch(listOf(config.sourceFolder, "${config.sourceFolder}/${config.blogs}")) { buildBlog(config) }
        }
        else -> throw IllegalArgumentException("Unknown build type: ${args.first()}.\n ${help()}")
    }

}

private fun help(): String {
    return """
        buildType: build-site,watch-site,build-blog,watch-blog
        path: ./website/src
        ex: java -jar simple-site.jar build-site ./website/src
    """.trimIndent()
}
