package blogBuilder

class SiteConfig(
    val blogs: String,
    val tabTitle: String = "Home",
    val toc: Boolean = false,
    val singlePageToc: Boolean = false,
    val tocTitle: String = "Table of Contents",
    val homeLink: String = "",
) {
    var sourceFolder = ""
}
