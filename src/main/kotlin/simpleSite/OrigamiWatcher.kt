package simpleSite.simpleSite

import simpleSite.buildSite
import simpleSite.readConfig
import simpleSite.watch
import java.io.File

fun main() {
    val folderPath = readConfig()["folderPath"]!! as String
    val cssPath = "$folderPath/css"
    watch(folderPath, cssPath, ::buildSite)
}