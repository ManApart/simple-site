package simpleSite.blogBuilder

import blogBuilder.buildBlog
import simpleSite.buildSite
import simpleSite.readConfig
import simpleSite.watch
import java.io.File

fun main() {
    val folderPath = readConfig()["blogPath"]!! as String
    watch(folderPath, "$folderPath/blogs", ::buildBlog)
}