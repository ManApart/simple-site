package simpleSite

import watch

fun main() {
    val folderPath = readConfig()["folderPath"]!! as String
    val cssPath = "$folderPath/css"
    watch(folderPath, cssPath) { buildSite(folderPath) }
}