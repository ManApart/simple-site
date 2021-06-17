package simpleSite.blogBuilder

import blogBuilder.buildBlog
import simpleSite.readConfig
import simpleSite.watch

fun main() {
    val config = readConfig()
    val folderPath = config["blogPath"]!! as String
    val subPath = config["blogSubPath"]!! as String

    watch(folderPath, "$folderPath/blogs") { buildBlog(folderPath, subPath) }
}