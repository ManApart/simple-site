package blogBuilder

import simpleSite.readSiteConfig
import watch

fun main() {
    val config = readSiteConfig()

    watch(listOf(config.sourceFolder, "${config.sourceFolder}/${config.blogs}")) { buildBlog(config) }
}