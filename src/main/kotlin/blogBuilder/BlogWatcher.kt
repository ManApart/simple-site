package simpleSite.blogBuilder

import blogBuilder.buildBlog
import simpleSite.readConfig
import simpleSite.readSiteConfig
import simpleSite.watch

fun main() {
    val config = readSiteConfig()
    val folderPath = config["folderPath"]!! as String
    val subPath = config["blogs"]!! as String
    val tabTitle = config["tabTitle"]!! as String

    watch(folderPath, "$folderPath/$subPath") { buildBlog(folderPath, subPath, tabTitle) }
}