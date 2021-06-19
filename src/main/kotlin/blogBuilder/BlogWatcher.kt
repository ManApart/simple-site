package simpleSite.blogBuilder

import blogBuilder.buildBlog
import simpleSite.readSiteConfig
import simpleSite.watch

fun main() {
    val config = readSiteConfig()

    watch(config.sourceFolder, "${config.sourceFolder}/${config.blogs}") { buildBlog(config) }
}