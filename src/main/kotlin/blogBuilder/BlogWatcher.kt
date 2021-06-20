package blogBuilder

import simpleSite.readSiteConfig
import watch

fun main() {
    val config = readSiteConfig()

    watch(config.sourceFolder, "${config.sourceFolder}/${config.blogs}") { buildBlog(config) }
}