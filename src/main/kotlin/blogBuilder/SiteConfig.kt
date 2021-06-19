package simpleSite.blogBuilder

class SiteConfig(
    val blogs: String,
    val tabTitle: String = "Home",
    val toc: Boolean = false,
    val tocTitle: String = "Table of Contents"
) {
    var sourceFolder = ""
}
